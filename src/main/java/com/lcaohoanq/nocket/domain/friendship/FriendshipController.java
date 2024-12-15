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
import java.time.LocalDateTime;
import java.util.Objects;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    public final FriendshipRepository friendshipRepository;
    private final IUserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> getAll(
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully get all friendships by user id")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.findByUser(user))
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
        User user = userService.findByUsername(userDetails.getUsername());

        User addressee = userRepository
            .findById(friendshipRequest.addresseeId())
            .orElseThrow(() -> new MalformBehaviourException("addressee not found"));

        if (friendshipRepository.findByRequesterAndAddressee(user, addressee) != null) {
            throw new MalformBehaviourException("Friend request already sent");
        }

        if (Objects.equals(user.getId(), addressee.getId())) {
            throw new MalformBehaviourException("Cannot send friend request to yourself");
        }

        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully create friend request")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.save(Friendship.builder()
                                                    .requester(user)
                                                    .addressee(addressee)
                                                    .status(FriendShipStatus.PENDING)
                                                    .createdAt(LocalDateTime.now())
                                                    .updatedAt(LocalDateTime.now())
                                                    .build()))
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

        User requester = userService.findByUsername(userDetails.getUsername());
        User addressee = userMapper.toUser(userService.findUserById(request.addresseeId()));

        Friendship friendship = friendshipRepository.findByRequesterAndAddressee(requester,
                                                                                 addressee);

        if (friendship == null) {
            throw new MalformBehaviourException("Friend request not found");
        }

        // Handle different actions
        switch (request.action()) {
            case ACCEPTED:
                if (friendship.getStatus() == FriendShipStatus.ACCEPTED) {
                    throw new MalformBehaviourException("Friend request already accepted");
                }
                friendship.setStatus(FriendShipStatus.ACCEPTED);
                return buildResponse("Successfully accepted friend request", friendship);

            case DECLINED:
                if (friendship.getStatus() == FriendShipStatus.DECLINED) {
                    throw new MalformBehaviourException("Friend request already declined");
                }
                friendship.setStatus(FriendShipStatus.DECLINED);
                return buildResponse("Successfully declined friend request", friendship);

            case UNFRIENDED:
                if (friendship.getStatus() == FriendShipStatus.UNFRIENDED) {
                    throw new MalformBehaviourException("Friendship already ended");
                }
                friendship.setStatus(FriendShipStatus.UNFRIENDED);
                return buildResponse("Successfully unfriended", friendship);

            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

    private ResponseEntity<ApiResponse<?>> buildResponse(String message, Friendship friendship) {
        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message(message)
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(friendshipRepository.save(friendship))
                .build()
        );
    }

}
