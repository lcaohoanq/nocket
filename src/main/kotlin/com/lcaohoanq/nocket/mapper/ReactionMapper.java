package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.reaction.Reaction;
import com.lcaohoanq.nocket.domain.reaction.ReactionPort;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    ReactionPort.ReactionResponse toReactionResponse(Reaction reaction);
    
}
