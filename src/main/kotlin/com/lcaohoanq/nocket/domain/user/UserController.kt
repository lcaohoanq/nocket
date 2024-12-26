package com.lcaohoanq.nocket.domain.user

import com.lcaohoanq.nocket.api.ApiResponse
import com.lcaohoanq.nocket.api.PageResponse
import com.lcaohoanq.nocket.api.ApiConstant
import com.lcaohoanq.nocket.domain.localization.MessageKey
import com.lcaohoanq.nocket.domain.user.UserPort.UpdateUserDTO
import com.lcaohoanq.nocket.domain.user.UserPort.UserResponse
import com.lcaohoanq.nocket.exception.MethodArgumentNotValidException
import com.lcaohoanq.nocket.mapper.UserMapper
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*

@Slf4j
@RequestMapping("${ApiConstant.API_PREFIX}/users")
@RestController
class UserController(
    private val userService: IUserService,
    private val userMapper: UserMapper
) {

    @GetMapping("") //@PreAuthorize("permitAll()")
    //can use or not but must implement on both WebSecurityConfig and JwtTokenFilter
    fun fetchUser(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<PageResponse<UserResponse>> =
        ResponseEntity.ok(userService.fetchUser(PageRequest.of(page, limit)))

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<UserResponse>> = ResponseEntity.ok(
        ApiResponse.builder<UserResponse>()
            .message("Successfully get user by id")
            .isSuccess(true)
            .statusCode(HttpStatus.OK.value())
            .data(userService.findUserById(id))
            .build()
    )

    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun takeUserDetailsFromToken(): ResponseEntity<UserResponse> =
        ResponseEntity.ok(
            userMapper.toUserResponse(
                userService.findByUsername(
                    (SecurityContextHolder.getContext().authentication.principal as UserDetails).username
                )
            )
        )

    @PutMapping("/details/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun updateUserDetails(
        @PathVariable userId: UUID,
        @RequestBody updatedUserDTO: @Valid UpdateUserDTO,
        result: BindingResult
    ): ResponseEntity<ApiResponse<UserResponse>> {
        if (result.hasErrors()) {
            throw MethodArgumentNotValidException(result)
        }

        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails
        val user = userService.findByUsername(userDetails.username)
        // Ensure that the user making the request matches the user being updated
        if (user.id != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        return ResponseEntity.ok(
            ApiResponse.builder<UserResponse>()
                .message(MessageKey.UPDATE_USER_SUCCESSFULLY)
                .data(userMapper.toUserResponse(userService.updateUser(userId, updatedUserDTO)))
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .build()
        )
    }

    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun blockOrEnable(
        @PathVariable @Valid userId: UUID,
        @PathVariable @Valid active: Int
    ): ResponseEntity<String> {
        userService.blockOrEnable(userId, active > 0)
        return ResponseEntity.ok(if (active > 0) "Successfully enabled the user." else "Successfully blocked the user.")
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<*> {
        userService.softDeleteUser(id)
        return ResponseEntity.ok("Delete user successfully")
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    fun restoreUser(@PathVariable id: UUID): ResponseEntity<*> {
        userService.restoreUser(id)
        return ResponseEntity.ok("Restore user successfully")
    }

    @MessageMapping("/user.addUser")
    @SendTo("/topic/public")
    fun addUser(
        @Payload user: User
    ): User {
        println("Received user: $user")
        userService.saveUser(user)
        return user
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/topic/public")
    fun disconnectUser(
        @Payload user: User
    ): User {
        userService.disconnect(user)
        return user
    }

    @GetMapping("/users")
    fun findConnectedUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(
            userService.findConnectedUsers()
        )
    }
}
