package com.lcaohoanq.nocket.domain.friendship

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.enums.FriendShipStatus
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.UUID

interface FriendshipPort {

    data class FriendShipRequest(
        @NotNull(message = "Addressee id is required")
        @JsonProperty("addressee_id")
        val addresseeId: UUID
    )
    
    data class FriendshipResponse(
        val id: UUID,
        val status: FriendShipStatus,
        val user1: User,
        val user2: User,
        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        val createdAt: LocalDateTime,
        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        val updatedAt: LocalDateTime,
    )
    
    data class FriendShipUpdateDTO(
        @NotNull(message = "Addresses Id is required")
        @JsonProperty("addressee_id")
        val addresseeId: UUID,
        val action: FriendShipStatus
    )

}