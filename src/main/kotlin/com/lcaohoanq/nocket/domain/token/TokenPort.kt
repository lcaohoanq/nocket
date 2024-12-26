package com.lcaohoanq.nocket.domain.token

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.time.LocalDateTime
import java.util.*

interface TokenPort {

    @JsonPropertyOrder(
        "id",
        "access_token",
        "refresh_token",
        "token_type",
        "expires",
        "expires_refresh_token",
        "is_mobile"
    )
    data class TokenResponse(
        val id: UUID,
        @JsonProperty("access_token") val token: String,
        @JsonProperty("refresh_token") val refreshToken: String,
        @JsonProperty("token_type") val tokenType: String,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        @JsonProperty("expires")
        val expirationDate: LocalDateTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        @JsonProperty("expires_refresh_token")
        val refreshExpirationDate: LocalDateTime,

        @JsonProperty("is_mobile") val isMobile: Boolean,

        @JsonIgnore val revoked: Boolean,
        @JsonIgnore val expired: Boolean
    )

}