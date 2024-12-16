package com.lcaohoanq.nocket.domain.friendship;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record FriendshipRequest(
    @NotNull(message = "Addressee id is required")
    @JsonProperty("addressee_id")
    UUID addresseeId
) {}
