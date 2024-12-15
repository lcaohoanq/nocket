package com.lcaohoanq.nocket.domain.friendship;

import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.enums.FriendShipStatus;

public record FriendshipResponse(
    Long id,
    FriendShipStatus status,
    User requester,
    User addressee
) {

}
