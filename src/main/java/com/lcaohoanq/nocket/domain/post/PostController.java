package com.lcaohoanq.nocket.domain.post;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import com.lcaohoanq.nocket.domain.asset.FileStoreService;
import com.lcaohoanq.nocket.domain.friendship.Friendship;
import com.lcaohoanq.nocket.domain.friendship.FriendshipRepository;
import com.lcaohoanq.nocket.domain.post.PostPort.PostResponse;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserService;
import com.lcaohoanq.nocket.enums.FriendShipStatus;
import com.lcaohoanq.nocket.enums.PostType;
import com.lcaohoanq.nocket.mapper.PostMapper;
import com.lcaohoanq.nocket.metadata.MediaMeta;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.prefix}/posts")
@RequiredArgsConstructor
public class PostController {

    private final UserService userService;
    private final PostRepository postRepository;
    private final IPostService postService;
    private final FriendshipRepository friendshipRepository;
    private final FileStoreService fileStoreService;
    private final PostMapper postMapper;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<List<PostPort.PostResponse>>> getAllPosts() {
        return ResponseEntity.ok().body(
            ApiResponse.<List<PostResponse>>builder()
                .message("Successfully get all posts")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(postRepository.findAll().stream().map(postMapper::toPostResponse).toList())
                .build()
        );
    }

    @GetMapping("/detail")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<PostPort.PostResponse>> getPostById(
        @RequestParam UUID id
    ) {
        return ResponseEntity.ok().body(
            ApiResponse.<PostPort.PostResponse>builder()
                .message("Successfully get post by id")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(postRepository.findById(id)
                          .map(postMapper::toPostResponse)
                          .orElseThrow(DataNotFoundException::new))
                .build()
        );
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<List<PostPort.PostResponse>>> getPostsOfUser(
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok().body(
            ApiResponse.<List<PostPort.PostResponse>>builder()
                .message("Successfully get all posts of user")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(postRepository.findByUser(user).stream().map(postMapper::toPostResponse)
                          .toList())
                .build()
        );
    }

    @GetMapping("/user-and-friends")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<List<PostPort.PostResponse>>> getPostsOfUserAndFriends() {
        // Get current authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User currentUser = userService.findByUsername(userDetails.getUsername());

        // Fetch friends (Assuming you have a method to get accepted friends)
        List<Friendship> friendships = friendshipRepository.findFriendshipsByUserAndStatus(
            currentUser,
            FriendShipStatus.ACCEPTED
        );

        List<User> friends = friendships.stream()
            .map(f -> f.getUser1().equals(currentUser) ? f.getUser2() : f.getUser1())
            .toList();

        // Create a list that includes the current user and their friends
        List<User> usersToFetchPostsFrom = new ArrayList<>();
        usersToFetchPostsFrom.add(currentUser);
        usersToFetchPostsFrom.addAll(friends);

        // Fetch posts for the current user and their friends
        List<Post> posts = postRepository.findByUserIn(usersToFetchPostsFrom);

        // Sort posts by creation date (most recent first)
        posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());

        return ResponseEntity.ok().body(
            ApiResponse.<List<PostPort.PostResponse>>builder()
                .message("Successfully retrieved posts for user and friends")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(posts.stream().map(postMapper::toPostResponse).toList())
                .build()
        );
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<PostPort.PostResponse>> createPost(
        @ModelAttribute("file") MultipartFile file
    ) throws IOException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        String filename = fileStoreService
            .storeFile(fileStoreService.validateProductImage(file));

        return ResponseEntity.ok().body(
            ApiResponse.<PostPort.PostResponse>builder()
                .message("Successfully upload post")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(
                    postMapper.toPostResponse(
                        postRepository.save(
                            Post.builder()
                                .postType(PostType.IMAGE)
                                .caption("Muon duoc ai do tang")
                                .mediaMeta(
                                    MediaMeta.builder()
                                        .fileName(filename)
                                        .fileSize(file.getSize())
                                        .imageUrl(filename)
                                        .mimeType(file.getContentType())
                                        .videoUrl(null)
                                        .build()
                                )
                                .user(user)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                        )
                    )
                )
                .build()
        );
    }

}
