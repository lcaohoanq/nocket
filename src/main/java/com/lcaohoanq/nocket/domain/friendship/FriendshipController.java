package com.lcaohoanq.nocket.domain.friendship;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.domain.user.IUserService;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserRepository;
import com.lcaohoanq.nocket.enums.FriendShipStatus;
import com.lcaohoanq.nocket.exception.MalformBehaviourException;
import com.lcaohoanq.nocket.exception.MethodArgumentNotValidException;
import com.lcaohoanq.nocket.mapper.UserMapper;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipRepository friendshipRepository;
    private final IUserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully retrieved all friendships")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.findAll())
                .build()
        );
    }

    @GetMapping("/user-and-status")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> getAllByUserAndStatus(
        @RequestParam UUID userId,
        @RequestParam FriendShipStatus status
    ) {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new MalformBehaviourException("User not found"));
        
        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully retrieved all friendships by user and status")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.findFriendshipsByUserAndStatus(
                    existingUser, status
                ))
                .build()
        );
    }

    @PostMapping("/friend-request")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> createFriendRequest(
        @Valid @RequestBody FriendshipRequest friendshipRequest,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(result);
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User requester = userService.findByUsername(userDetails.getUsername());

        User addressee = userRepository
            .findById(friendshipRequest.addresseeId())
            .orElseThrow(() -> new MalformBehaviourException("Addressee not found"));

        // Prevent self-friending
        if (requester.getId().equals(addressee.getId())) {
            throw new MalformBehaviourException("Cannot send friend request to yourself");
        }

        // Check if friendship already exists
        Optional<Friendship> existingFriendship = friendshipRepository
            .findFriendshipBetweenUsers(requester, addressee);

        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();
            if (friendship.getStatus() == FriendShipStatus.PENDING) {
                throw new MalformBehaviourException("Friend request already sent");
            }
            if (friendship.getStatus() == FriendShipStatus.ACCEPTED) {
                throw new MalformBehaviourException("Users are already friends");
            }
        }

        // Create new friendship
        Friendship newFriendship = Friendship.builder()
            .user1(requester)
            .user2(addressee)
            .status(FriendShipStatus.PENDING)
            .build();
        newFriendship.normalizeUsers(); // Ensure consistent user order

        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully created friend request")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.save(newFriendship))
                .build()
        );
    }

    @PutMapping("/friend-action")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> handleFriendRequest(
        @Valid @RequestBody FriendShipUpdateDTO request
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        User currentUser = userService.findByUsername(userDetails.getUsername());
        User otherUser = userMapper.toUser(userService.findUserById(request.addresseeId()));

        // Find existing friendship
        Friendship friendship = friendshipRepository
            .findFriendshipBetweenUsers(currentUser, otherUser)
            .orElseThrow(() -> new MalformBehaviourException("Friend request not found"));

        // Validate and update friendship based on action
        switch (request.action()) {
            case ACCEPTED:
                validateFriendshipAction(friendship, FriendShipStatus.PENDING,
                                         "Friend request can only be accepted when pending");
                friendship.setStatus(FriendShipStatus.ACCEPTED);
                break;

            case DECLINED:
                validateFriendshipAction(friendship, FriendShipStatus.PENDING,
                                         "Friend request can only be declined when pending");
                friendship.setStatus(FriendShipStatus.DECLINED);
                break;

            case BLOCKED:
                // Can block at any point
                friendship.setStatus(FriendShipStatus.BLOCKED);
                break;

            default:
                throw new IllegalArgumentException("Invalid action");
        }

        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully processed friend request")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.save(friendship))
                .build()
        );
    }

    // Helper method to validate friendship status before action
    private void validateFriendshipAction(
        Friendship friendship,
        FriendShipStatus expectedStatus,
        String errorMessage
    ) {
        if (friendship.getStatus() != expectedStatus) {
            throw new MalformBehaviourException(errorMessage);
        }
    }

    // Additional method to get pending friend requests
    @GetMapping("/pending-requests")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> getPendingFriendRequests() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User currentUser = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully retrieved pending friend requests")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.findFriendshipsByUserAndStatus(
                    currentUser, FriendShipStatus.PENDING
                ))
                .build()
        );
    }
}