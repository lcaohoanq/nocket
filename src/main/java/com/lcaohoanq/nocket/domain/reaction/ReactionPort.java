package com.lcaohoanq.nocket.domain.reaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lcaohoanq.nocket.enums.EReaction;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ReactionPort {

    record ReactionDTO(EReaction reaction) {}
    
    @JsonPropertyOrder(
        {
            "id",
            "reaction",
            "created_at",
            "updated_at"
        }
    )
    record ReactionResponse(
        UUID id,
        EReaction reaction,
        
        @JsonIgnore
        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime createdAt,
        
        @JsonIgnore
        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime updatedAt
    ) {}
    
}
