package com.lcaohoanq.nocket.domain.token;

import com.lcaohoanq.nocket.domain.token.TokenPort.TokenResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    
    TokenResponse toTokenResponse(Token token);

}
