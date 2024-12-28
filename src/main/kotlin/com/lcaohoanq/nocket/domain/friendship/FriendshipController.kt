package com.lcaohoanq.nocket.domain.friendship

import com.lcaohoanq.nocket.api.ApiResponse
import com.lcaohoanq.nocket.domain.friendship.FriendshipPort.FriendShipRequest
import com.lcaohoanq.nocket.domain.friendship.FriendshipPort.FriendShipUpdateDTO
import com.lcaohoanq.nocket.domain.user.IUserService
import com.lcaohoanq.nocket.domain.user.UserRepository
import com.lcaohoanq.nocket.enums.FriendShipStatus
import com.lcaohoanq.nocket.exception.MalformBehaviourException
import com.lcaohoanq.nocket.exception.MethodArgumentNotValidException
import com.lcaohoanq.nocket.domain.user.UserMapper
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("\${api.prefix}/friendships")
@RequiredArgsConstructor
class FriendshipController(
    private val friendshipRepository: FriendshipRepository,
    private val userService: IUserService,
    private val userMapper: UserMapper,
    private val userRepository: UserRepository
) {

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @GetMapping("/all")
    fun getAll(): ResponseEntity<ApiResponse<*>> = ResponseEntity.ok().body(
        ApiResponse.builder<Any>()
            .message("Successfully retrieved all friendships")
            .isSuccess(true)
            .statusCode(HttpStatus.OK.value())
            .data(friendshipRepository.findAll())
            .build()
    )

    @GetMapping("/user-and-status")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun getAllByUserAndStatus(
        @RequestParam userId: UUID,
        @RequestParam status: FriendShipStatus
    ): ResponseEntity<ApiResponse<*>> {
        val existingUser = userRepository!!.findById(userId)
            .orElseThrow { MalformBehaviourException("User not found") }

        return ResponseEntity.ok().body(
            ApiResponse.builder<Any>()
                .message("Successfully retrieved all friendships by user and status")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(
                    friendshipRepository.findFriendshipsByUserAndStatus(
                        existingUser, status
                    )
                )
                .build()
        )
    }

    @PostMapping("/friend-request")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun createFriendRequest(
        @RequestBody friendshipRequest: @Valid FriendShipRequest,
        result: BindingResult
    ): ResponseEntity<ApiResponse<*>> {
        if (result.hasErrors()) {
            throw MethodArgumentNotValidException(result)
        }

        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails
        val requester = userService.findByUsername(userDetails.username)

        val addressee = userRepository
            .findById(friendshipRequest.addresseeId)
            .orElseThrow { MalformBehaviourException("Addressee not found") }

        // Prevent self-friending
        if (requester.id == addressee.id) {
            throw MalformBehaviourException("Cannot send friend request to yourself")
        }

        // Check if friendship already exists
        val existingFriendship = friendshipRepository
            .findFriendshipBetweenUsers(requester, addressee)

        if (existingFriendship.isPresent) {
            val friendship = existingFriendship.get()
            if (friendship.status == FriendShipStatus.PENDING) {
                throw MalformBehaviourException("Friend request already sent")
            }
            if (friendship.status == FriendShipStatus.ACCEPTED) {
                throw MalformBehaviourException("Users are already friends")
            }

            if (friendship.status == FriendShipStatus.BLOCKED) {
                throw MalformBehaviourException(
                    "Friend request cannot be sent to blocked user"
                )
            }

            if (friendship.status == FriendShipStatus.DECLINED) {
                // Update the status back to PENDING for a new friend request
                friendship.status = FriendShipStatus.PENDING
                friendshipRepository.save(friendship)
                return ResponseEntity.ok().body(
                    ApiResponse.builder<Any>()
                        .message("Friend request sent again")
                        .isSuccess(true)
                        .statusCode(HttpStatus.OK.value())
                        .data(friendship)
                        .build()
                )
            }
        }

        // Create new friendship
        val newFriendship = Friendship()
        newFriendship.user1 = requester
        newFriendship.user2 = addressee
        newFriendship.status = FriendShipStatus.PENDING

        newFriendship.normalizeUsers() // Ensure consistent user order

        return ResponseEntity.ok().body(
            ApiResponse.builder<Any>()
                .message("Successfully created friend request")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.save(newFriendship))
                .build()
        )
    }

    @PutMapping("/friend-action")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun handleFriendRequest(
        @RequestBody request: @Valid FriendShipUpdateDTO
    ): ResponseEntity<ApiResponse<*>> {
        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails

        val currentUser = userService.findByUsername(userDetails.username)
        val otherUser = userMapper.toUser(userService.findUserById(request.addresseeId))

        // Find existing friendship
        val friendship = friendshipRepository
            .findFriendshipBetweenUsers(currentUser, otherUser)
            .orElseThrow { MalformBehaviourException("Friend request not found") }

        // Validate and update friendship based on action
        when (request.action) {
            FriendShipStatus.ACCEPTED -> {
                validateFriendshipAction(
                    friendship, FriendShipStatus.PENDING,
                    "Friend request can only be accepted when pending"
                )
                friendship.status = FriendShipStatus.ACCEPTED
            }

            FriendShipStatus.DECLINED -> {
                validateFriendshipAction(
                    friendship, FriendShipStatus.PENDING,
                    "Friend request can only be declined when pending"
                )
                friendship.status = FriendShipStatus.DECLINED
            }

            FriendShipStatus.BLOCKED ->                 // Can block at any point
                friendship.status = FriendShipStatus.BLOCKED

            else -> throw IllegalArgumentException("Invalid action")
        }

        return ResponseEntity.ok().body(
            ApiResponse.builder<Any>()
                .message("Successfully processed friend request")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.save(friendship))
                .build()
        )
    }

    // Helper method to validate friendship status before action
    private fun validateFriendshipAction(
        friendship: Friendship,
        expectedStatus: FriendShipStatus,
        errorMessage: String
    ) {
        if (friendship.status != expectedStatus) {
            throw MalformBehaviourException(errorMessage)
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @GetMapping("/pending-requests")
    fun getPendingFriendRequests(): ResponseEntity<ApiResponse<*>> {
        // Additional method to get pending friend requests

        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails
        val currentUser =
            userService.findByUsername(userDetails.username)

        return ResponseEntity.ok().body(
            ApiResponse.builder<Any>()
                .message("Successfully retrieved pending friend requests")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(
                    friendshipRepository.findFriendshipsByUserAndStatus(
                        currentUser, FriendShipStatus.PENDING
                    )
                )
                .build()
        )
    }
}