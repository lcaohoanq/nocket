package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "avatar", source = "user", qualifiedByName = "getFirstAvatar")
    UserResponse toUserResponse(User user);

    @Named("getFirstAvatar")
    default String getFirstAvatar(User user) {
        return user.getAvatars().stream().findFirst()
            .map(avatar -> avatar.getMediaMeta().getImageUrl())
            .orElse(null);
    }
    
    User toUser(UserResponse userResponse);
    
}
