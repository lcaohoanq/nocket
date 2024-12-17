package com.lcaohoanq.nocket.domain.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lcaohoanq.nocket.constant.BusinessNumber;
import com.lcaohoanq.nocket.domain.reaction.PostReaction;
import com.lcaohoanq.nocket.domain.user.UserPort;
import com.lcaohoanq.nocket.enums.PostType;
import com.lcaohoanq.nocket.metadata.MediaMeta;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostPort {

    record PostDTO(
        UserPort.UserResponse postOwner,
        @Size(
            max = BusinessNumber.MAXIMUM_POST_CAPTION_LENGTH,
            message = """
            Caption must be less than or equal to {max} characters.
            """
        )
        String caption,
        @JsonProperty("post_type") PostType postType,
        @JsonProperty("media_meta") MediaMeta mediaMeta,
        List<PostReaction> reactions
    ) {

    }

    @JsonPropertyOrder(
        {
            "id",
            "post_owner",
            "caption",
            "post_type",
            "media_meta",
            "reactions",
            "created_at",
            "updated_at"
        }
    )
    record PostResponse(
        UUID id,
        @JsonProperty("post_owner") UserPort.UserResponse postOwner,
        String caption,
        @JsonProperty("post_type") PostType postType,
        @JsonProperty("media_meta") MediaMeta mediaMeta,
        List<PostReaction> reactions,
        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime updatedAt
    ) {

    }

}
