package com.lcaohoanq.nocket.domain.wallet

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.time.LocalDateTime
import java.util.*

interface WalletPort {
    
    @JsonPropertyOrder(
        "id", "balance", "created_at", "updated_at"
    )
    data class WalletResponse(
        val id: UUID,
        val balance: Float,
        @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") @field:JsonProperty(
            "created_at"
        ) @param:JsonProperty("created_at") @param:JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS"
        ) val createdAt: LocalDateTime,
        @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") @field:JsonProperty(
            "updated_at"
        ) @param:JsonProperty("updated_at") @param:JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS"
        ) val updatedAt: LocalDateTime
    )
    
    data class WalletRequest(
        val balance: Float,
        val userId: Long
    )
}
