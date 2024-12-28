package com.lcaohoanq.nocket.domain.reaction

import com.lcaohoanq.nocket.api.ApiResponse
import com.lcaohoanq.nocket.base.exception.DataNotFoundException
import com.lcaohoanq.nocket.domain.reaction.ReactionPort.ReactionDTO
import com.lcaohoanq.nocket.domain.reaction.ReactionPort.ReactionResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RequestMapping("\${api.prefix}/reactions")
@RestController
class ReactionController(
    private val reactionRepository: ReactionRepository,
    private val reactionMapper: ReactionMapper,
) {

    @GetMapping("/all")
    fun getAllReactions(): ResponseEntity<ApiResponse<List<ReactionResponse>>> = ResponseEntity.ok(
        ApiResponse.builder<List<ReactionResponse>>()
            .message("Get all reactions success")
            .statusCode(HttpStatus.OK.value())
            .isSuccess(true)
            .data(
                reactionRepository.findAll().stream()
                    .map { reaction: Reaction? -> reactionMapper.toReactionResponse(reaction) }
                    .toList())
            .build()
    )

    @GetMapping("/detail")
    fun getReactionById(
        @RequestParam id: UUID
    ): ResponseEntity<ApiResponse<ReactionResponse>> = ResponseEntity.ok(
        ApiResponse.builder<ReactionResponse>()
            .message("Get reaction by id success")
            .statusCode(HttpStatus.OK.value())
            .isSuccess(true)
            .data(
                reactionRepository.findById(id).map { reaction: Reaction? ->
                    reactionMapper.toReactionResponse(
                        reaction
                    )
                }
                    .orElseThrow { DataNotFoundException("Reaction not found") })
            .build()
    )

    @PostMapping("/generate-reaction")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun generateReaction(
        @RequestBody reaction: ReactionDTO
    ): ResponseEntity<ApiResponse<ReactionResponse>> {
        val newReaction = Reaction()
        newReaction.setReaction(reaction.reaction)

        return ResponseEntity.ok(
            ApiResponse.builder<ReactionResponse>()
                .message("Generate reaction success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(reactionMapper.toReactionResponse(reactionRepository.save(newReaction)))
                .build()
        )
    }
}
