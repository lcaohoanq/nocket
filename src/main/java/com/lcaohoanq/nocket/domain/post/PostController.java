package com.lcaohoanq.nocket.domain.post;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.domain.friendship.FriendshipRepository;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserService;
import com.lcaohoanq.nocket.enums.FriendShipStatus;
import com.lcaohoanq.nocket.enums.PostType;
import com.lcaohoanq.nocket.metadata.MediaMeta;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/posts")
@RequiredArgsConstructor
public class PostController {

    private final UserService userService;
    private final PostRepository postRepository;
    private final IPostService postService;
    private final FriendshipRepository friendshipRepository;

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> getPostsOfUser(
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully get all friendships by user id")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(postRepository.findByUser(user))
                .build()
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> getPostsOfUserAndFriends() {
        // Get current authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User currentUser = userService.findByUsername(userDetails.getUsername());

        // Fetch friends (Assuming you have a method to get accepted friends)
        List<User> friends = friendshipRepository.findFriendsByUserAndStatus(
            currentUser,
            FriendShipStatus.ACCEPTED
        );

        // Create a list that includes the current user and their friends
        List<User> usersToFetchPostsFrom = new ArrayList<>();
        usersToFetchPostsFrom.add(currentUser);
        usersToFetchPostsFrom.addAll(friends);

        // Fetch posts for the current user and their friends
        List<Post> posts = postRepository.findByUserIn(usersToFetchPostsFrom);

        // Sort posts by creation date (most recent first)
        posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());

        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully retrieved posts for user and friends")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(posts)
                .build()
        );
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> createPost(
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok().body(
            ApiResponse.builder()
                .message("Successfully create post")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(
                    postRepository.save(
                        Post.builder()
                            .postType(PostType.IMAGE)
                            .caption("Muon duoc ai do tang")
                            .mediaMeta(
                                MediaMeta.builder()
                                    .imageUrl("https://www.google.com")
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .fileType("image")
                                    .mimeType("image")
                                    .build()
                            )
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
                    )
                )
                .build()
        );
    }

}
