package com.lcaohoanq.nocket.domain.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostImageDTO(
    @JsonProperty("post_id")
    @Min(value = 1, message = "Post ID must be greater than 0")
    @NotNull(message = "Post ID is required")
    Long postId,

    @JsonProperty("image_url")
    @Size(min = 5, max = 300, message = "Image URL must be between 5 and 300 characters")
    String imageUrl
) {

}
