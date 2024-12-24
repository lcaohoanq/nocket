package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.token.Token;
import com.lcaohoanq.nocket.domain.token.TokenPort.TokenResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    
    TokenResponse toTokenResponse(Token token);

}
