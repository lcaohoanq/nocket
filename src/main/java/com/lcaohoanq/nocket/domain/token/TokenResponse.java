package com.lcaohoanq.nocket.domain.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import java.util.UUID;
@JsonPropertyOrder(
    {
        "id",
        "access_token",
        "refresh_token",
        "token_type",
        "expires",
        "expires_refresh_token",
        "is_mobile"
    }
)
public record TokenResponse(
    UUID id,
    @JsonProperty("access_token") String token,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("token_type") String tokenType,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    @JsonProperty("expires")
    LocalDateTime expirationDate,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    @JsonProperty("expires_refresh_token")
    LocalDateTime refreshExpirationDate,

    @JsonProperty("is_mobile") boolean isMobile,

    @JsonIgnore boolean revoked,
    @JsonIgnore boolean expired
) {

}
