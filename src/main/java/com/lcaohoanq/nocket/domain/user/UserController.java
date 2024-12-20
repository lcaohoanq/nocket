package com.lcaohoanq.nocket.domain.user;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.api.PageResponse;
import com.lcaohoanq.nocket.constant.MessageKey;
import com.lcaohoanq.nocket.enums.FriendShipStatus;
import com.lcaohoanq.nocket.exception.MethodArgumentNotValidException;
import com.lcaohoanq.nocket.mapper.UserMapper;
import jakarta.validation.Valid;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    IUserService userService;
    UserMapper userMapper;

    @GetMapping("")
    //@PreAuthorize("permitAll()")
    //can use or not but must implement on both WebSecurityConfig and JwtTokenFilter
    public ResponseEntity<PageResponse<UserPort.UserResponse>> fetchUser(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(userService.fetchUser(PageRequest.of(page, limit)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserPort.UserResponse>> getUserById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
            ApiResponse.<UserPort.UserResponse>builder()
                .message("Successfully get user by id")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(userService.findUserById(id))
                .build()
        );
    }

    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<UserPort.UserResponse> takeUserDetailsFromToken() throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return ResponseEntity.ok(
            userMapper.toUserResponse(userService.findByUsername(userDetails.getUsername())));
    }

    @PutMapping("/details/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<UserPort.UserResponse>> updateUserDetails(
        @PathVariable UUID userId,
        @Valid @RequestBody UserPort.UpdateUserDTO updatedUserDTO,
        BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(result);
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        // Ensure that the user making the request matches the user being updated
        if (!Objects.equals(user.getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
            ApiResponse.<UserPort.UserResponse>builder()
                .message(MessageKey.UPDATE_USER_SUCCESSFULLY)
                .data(userMapper.toUserResponse(userService.updateUser(userId, updatedUserDTO)))
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<String> blockOrEnable(
        @Valid @PathVariable UUID userId,
        @Valid @PathVariable int active
    ) {
        userService.blockOrEnable(userId, active > 0);
        String message =
            active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(message);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("Delete user successfully");
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> restoreUser(@PathVariable UUID id) {
        userService.restoreUser(id);
        return ResponseEntity.ok("Restore user successfully");
    }
    
}
