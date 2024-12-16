package com.lcaohoanq.nocket.domain.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lcaohoanq.nocket.domain.token.TokenResponse;
import com.lcaohoanq.nocket.domain.user.UserResponse;

@JsonPropertyOrder({
    "token",
    "user"
})
@JsonInclude(Include.NON_NULL)
public record LoginResponse(
    TokenResponse token,
    //user's detail
    UserResponse user
) {}
