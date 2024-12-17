package com.lcaohoanq.nocket.domain.reaction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lcaohoanq.nocket.domain.post.PostPort.PostResponse;
import com.lcaohoanq.nocket.domain.reaction.ReactionPort.ReactionResponse;
import com.lcaohoanq.nocket.domain.user.UserPort.UserResponse;
import java.util.UUID;

public interface PostReactionPort {

    @JsonPropertyOrder(
        {
            "id",
            "user",
            "post",
            "reaction"
        }
    )
    record PostReactionResponse(
        UUID id,
        UserResponse user,
        PostResponse post,
        ReactionResponse reaction
    ){}
    
}
