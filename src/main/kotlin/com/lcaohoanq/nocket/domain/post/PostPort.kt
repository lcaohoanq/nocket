package com.lcaohoanq.nocket.domain.post

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.lcaohoanq.nocket.constant.BusinessNumber
import com.lcaohoanq.nocket.constant.ValidationMessage
import com.lcaohoanq.nocket.domain.reaction.PostReaction
import com.lcaohoanq.nocket.domain.user.UserPort.UserResponse
import com.lcaohoanq.nocket.enums.PostType
import com.lcaohoanq.nocket.metadata.MediaMeta
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

interface PostPort {
    data class PostDTO(
        val postOwner: UserResponse,
        val caption: @Size(
            max = BusinessNumber.MAXIMUM_POST_CAPTION_LENGTH,
            message = ValidationMessage.CAPTION_MAX_LENGTH_MSG
        ) String?,
        @JsonProperty("post_type") val postType: PostType,
        @JsonProperty("media_meta") val mediaMeta: MediaMeta,
        val reactions: List<PostReaction>
    )

    @JsonPropertyOrder(
        "id",
        "post_owner",
        "caption",
        "post_type",
        "media_meta",
        "reactions",
        "created_at",
        "updated_at"
    )
    data class PostResponse(
        val id: UUID,
        @JsonProperty("post_owner") val postOwner: UserResponse,
        val caption: String,
        @JsonProperty("post_type") val postType: PostType,
        @JsonProperty("media_meta") val mediaMeta: MediaMeta,
        val reactions: List<PostReaction>,
        @JsonProperty("created_at") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        val createdAt: LocalDateTime,
        @JsonProperty("updated_at") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        val updatedAt: LocalDateTime
    )
}
