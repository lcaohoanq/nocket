package com.lcaohoanq.nocket.domain.reaction

import com.lcaohoanq.nocket.api.ApiResponse
import com.lcaohoanq.nocket.base.exception.DataNotFoundException
import com.lcaohoanq.nocket.domain.post.PostRepository
import com.lcaohoanq.nocket.domain.reaction.PostReactionPort.PostReactionResponse
import com.lcaohoanq.nocket.domain.user.IUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("\${api.prefix}/posts-reactions")
class PostReactionController(
    private val postReactionRepository: PostReactionRepository,
    private val postRepository: PostRepository,
    private val userService: IUserService,
    private val reactionRepository: ReactionRepository,
    private val postReactionMapper: PostReactionMapper
) {

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @GetMapping("/all")
    fun getAllPostReactions(): ResponseEntity<ApiResponse<List<PostReactionResponse>>> {
        val postReactions = postReactionRepository.findAll()

        val postReactionResponses = postReactions.map { postReaction ->
            postReactionMapper.toPostReactionResponse(postReaction)
        }

        return ResponseEntity.ok(
            ApiResponse.builder<List<PostReactionResponse>>()
                .message("Get all post reactions success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(postReactionResponses)
                .build()
        )
    }

    @GetMapping("/post")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun getPostReactionsByPostId(
        @RequestParam id: UUID
    ): ResponseEntity<ApiResponse<List<PostReactionResponse>>> = ResponseEntity.ok(
        ApiResponse.builder<List<PostReactionResponse>>()
            .message("Get all post reactions by post id success")
            .statusCode(HttpStatus.OK.value())
            .isSuccess(true)
            .data(
                postReactionRepository.findByPostId(id).stream()
                    .map { postReaction: PostReaction? ->
                        postReactionMapper.toPostReactionResponse(
                            postReaction
                        )
                    }.toList()
            )
            .build()
    )

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun createPostReaction(
        @RequestBody postReactionDTO: PostReactionDTO
    ): ResponseEntity<ApiResponse<PostReactionResponse>> {
        val newExistingPost = postRepository
            .findById(postReactionDTO.postId)
            .orElseThrow { DataNotFoundException("Post not found") }

        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails
        val newUser = userService.findByUsername(userDetails.username)

        val newReaction = reactionRepository.findByReaction(postReactionDTO.reaction)
            .orElseThrow { DataNotFoundException("Reaction not found") }

        val postReaction = PostReaction()
        with(postReaction) {
            post = newExistingPost
            user = newUser
            reaction = newReaction
        }

        return ResponseEntity.ok(
            ApiResponse.builder<PostReactionResponse>()
                .message("Create post reaction success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(
                    postReactionMapper.toPostReactionResponse(
                        postReactionRepository.save(postReaction)
                    )
                )
                .build()
        )
    }
}
