package com.lcaohoanq.nocket.domain.friendship;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    FriendshipPort.FriendshipResponse toFriendshipResponse(FriendshipPort.FriendShipRequest friendshipRequest);
    Friendship toFriendship(FriendshipPort.FriendshipResponse friendshipResponse);
    
}
