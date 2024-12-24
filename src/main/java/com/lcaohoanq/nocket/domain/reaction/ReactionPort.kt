package com.lcaohoanq.nocket.domain.reaction

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.lcaohoanq.nocket.enums.EReaction
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonPropertyOrder
import java.time.LocalDateTime
import java.util.*

interface ReactionPort {
    data class ReactionDTO(val reaction: EReaction)

    @JsonPropertyOrder(
        "id", "reaction", "created_at", "updated_at"
    )
    data class ReactionResponse(
        val id: UUID,
        val reaction: EReaction,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") @JsonProperty(
            "created_at"
        ) @JsonIgnore val createdAt: LocalDateTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") @JsonProperty(
            "updated_at"
        ) @JsonIgnore val updatedAt: LocalDateTime
    )
}
