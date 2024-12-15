package com.lcaohoanq.nocket.domain.friendship;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record FriendshipRequest(
    @NotNull(message = "Addressee id is required")
    @JsonProperty("addressee_id")
    Long addresseeId
) {}
