package com.lcaohoanq.nocket.domain.reaction;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    ReactionPort.ReactionResponse toReactionResponse(Reaction reaction);
    
}
