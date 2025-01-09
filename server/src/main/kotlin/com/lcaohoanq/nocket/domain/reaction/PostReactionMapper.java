package com.lcaohoanq.nocket.domain.reaction;

import com.lcaohoanq.nocket.domain.post.PostMapper;
import com.lcaohoanq.nocket.domain.reaction.PostReactionPort.PostReactionResponse;
import com.lcaohoanq.nocket.domain.user.UserMapper;
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
