package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.friendship.Friendship;
import com.lcaohoanq.nocket.domain.friendship.FriendshipPort;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    FriendshipPort.FriendshipResponse toFriendshipResponse(FriendshipPort.FriendShipRequest friendshipRequest);
    Friendship toFriendship(FriendshipPort.FriendshipResponse friendshipResponse);
    
}
