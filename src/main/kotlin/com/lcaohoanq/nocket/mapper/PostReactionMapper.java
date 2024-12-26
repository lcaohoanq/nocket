package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.reaction.PostReaction;
import com.lcaohoanq.nocket.domain.reaction.PostReactionPort.PostReactionResponse;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {
        UserMapper.class,
        PostMapper.class,
        ReactionMapper.class
    }
)
public interface PostReactionMapper {
    
    PostReactionResponse toPostReactionResponse(PostReaction postReaction);

}
