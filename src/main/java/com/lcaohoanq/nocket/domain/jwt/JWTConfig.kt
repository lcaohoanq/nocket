package com.lcaohoanq.nocket.domain.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JWTConfig(
    var expiration: Int = 0,
    var secretKey: String = "",
    var expirationRefreshToken: Int = 0
)
