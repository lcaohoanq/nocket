package com.lcaohoanq.nocket.domain.reaction

import com.lcaohoanq.nocket.enums.EReaction
import jakarta.validation.constraints.NotNull
import java.util.*

data class PostReactionDTO(
    val postId: @NotNull(message = "Post Id is required") UUID,
    val reaction: EReaction
)
