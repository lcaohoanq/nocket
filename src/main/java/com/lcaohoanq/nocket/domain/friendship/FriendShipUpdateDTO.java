package com.lcaohoanq.nocket.domain.friendship;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lcaohoanq.nocket.enums.FriendShipStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record FriendShipUpdateDTO(
    @NotNull(message = "Addresses Id is required")
    @JsonProperty("addressee_id")
    UUID addresseeId,
    FriendShipStatus action
) {}
