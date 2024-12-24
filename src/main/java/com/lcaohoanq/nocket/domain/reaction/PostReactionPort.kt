package com.lcaohoanq.nocket.domain.reaction

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.lcaohoanq.nocket.domain.post.PostPort.PostResponse
import com.lcaohoanq.nocket.domain.reaction.ReactionPort.ReactionResponse
import com.lcaohoanq.nocket.domain.user.UserPort.UserResponse
import java.util.*

interface PostReactionPort {
    @JsonPropertyOrder(
        "id", "user", "post", "reaction"
    )
    data class PostReactionResponse(
        val id: UUID,
        val user: UserResponse,
        val post: PostResponse,
        val reaction: ReactionResponse
    )
}
