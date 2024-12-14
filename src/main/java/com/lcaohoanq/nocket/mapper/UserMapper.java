package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "walletId", source = "wallet.id")
    UserResponse toUserResponse(User user);

    User toUser(UserResponse userResponse);
    
}
