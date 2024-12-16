package com.lcaohoanq.nocket.domain.reaction;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import com.lcaohoanq.nocket.domain.post.IPostService;
import com.lcaohoanq.nocket.domain.post.Post;
import com.lcaohoanq.nocket.domain.post.PostRepository;
import com.lcaohoanq.nocket.domain.user.IUserService;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/posts-reactions")
@RequiredArgsConstructor
public class PostReactionController {

    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final IUserService userService;
    private final ReactionRepository reactionRepository;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> getAllPostReactions() {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .message("Get all post reactions success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(postReactionRepository.findAll())
                .build()
        );
    }
    
    @GetMapping("/post")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> getPostReactionsByPostId(
        @RequestParam UUID id
    ) {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .message("Get all post reactions by post id success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(postReactionRepository.findByPostId(id))
                .build()
        );
    }
    
    @PostMapping("")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> createPostReaction(
        @RequestBody PostReactionDTO postReactionDTO
    ) {

        Post existingPost = postRepository
            .findById(postReactionDTO.postId()).orElseThrow(() -> new DataNotFoundException("Post not found"));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        
        Reaction reaction = reactionRepository.findByReaction(postReactionDTO.reaction())
            .orElseThrow(() -> new DataNotFoundException("Reaction not found"));
        
        return ResponseEntity.ok(
            ApiResponse.builder()
                .message("Create post reaction success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(postReactionRepository.save(
                    PostReaction.builder()
                        .post(existingPost)
                        .user(user)
                        .reaction(reaction)
                        .build()
                ))
                .build()
        );
    }
    
    
}
