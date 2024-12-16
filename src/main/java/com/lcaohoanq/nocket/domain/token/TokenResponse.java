package com.lcaohoanq.nocket.domain.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record TokenResponse(
    String token,
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
