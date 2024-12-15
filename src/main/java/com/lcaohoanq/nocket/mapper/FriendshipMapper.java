package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.friendship.Friendship;
import com.lcaohoanq.nocket.domain.friendship.FriendshipRequest;
import com.lcaohoanq.nocket.domain.friendship.FriendshipResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    FriendshipResponse toFriendshipResponse(FriendshipRequest friendshipRequest);
    Friendship toFriendship(FriendshipResponse friendshipResponse);
    
}
