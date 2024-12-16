package com.lcaohoanq.nocket.domain.wallet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import java.util.UUID;

public interface WalletDTO {
    
    @JsonPropertyOrder({
        "id",
        "balance",
        "created_at",
        "updated_at"
    })
    record WalletResponse(
        UUID id,
        Float balance,
        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime updatedAt
    ) {}
    
    record WalletRequest(
        Float balance,
        Long userId
    ) {}

}
