package com.lcaohoanq.nocket.domain.reaction;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import com.lcaohoanq.nocket.domain.post.Post;
import com.lcaohoanq.nocket.domain.post.PostRepository;
import com.lcaohoanq.nocket.domain.reaction.PostReactionPort.PostReactionResponse;
import com.lcaohoanq.nocket.domain.user.IUserService;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.mapper.PostReactionMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final PostReactionMapper postReactionMapper;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<List<PostReactionResponse>>> getAllPostReactions() {
        return ResponseEntity.ok(
            ApiResponse.<List<PostReactionResponse>>builder()
                .message("Get all post reactions success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(
                    postReactionRepository
                        .findAll()
                        .stream()
                        .map(postReactionMapper::toPostReactionResponse)
                        .toList()
                )
                .build()
        );
    }

    @GetMapping("/post")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<List<PostReactionResponse>>> getPostReactionsByPostId(
        @RequestParam UUID id
    ) {
        return ResponseEntity.ok(
            ApiResponse.<List<PostReactionResponse>>builder()
                .message("Get all post reactions by post id success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(postReactionRepository.findByPostId(id).stream()
                          .map(postReactionMapper::toPostReactionResponse).toList())
                .build()
        );
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<PostReactionResponse>> createPostReaction(
        @RequestBody PostReactionDTO postReactionDTO
    ) {

        Post existingPost = postRepository
            .findById(postReactionDTO.getPostId())
            .orElseThrow(() -> new DataNotFoundException("Post not found"));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        Reaction reaction = reactionRepository.findByReaction(postReactionDTO.getReaction())
            .orElseThrow(() -> new DataNotFoundException("Reaction not found"));

        PostReaction postReaction = new PostReaction();
        postReaction.setPost(existingPost);
        postReaction.setUser(user);
        postReaction.setReaction(reaction);

        return ResponseEntity.ok(
            ApiResponse.<PostReactionResponse>builder()
                .message("Create post reaction success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(
                    postReactionMapper.toPostReactionResponse(
                        postReactionRepository.save(postReaction)))
                .build()
        );
    }


}
