package com.lcaohoanq.nocket.domain.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "avatar", source = "user", qualifiedByName = "getFirstAvatar")
    UserPort.UserResponse toUserResponse(User user);

    @Named("getFirstAvatar")
    default String getFirstAvatar(User user) {
        return user.getAvatars().stream().findFirst()
            .map(avatar -> {
                assert avatar.getMediaMeta() != null;
                return avatar.getMediaMeta().getImageUrl();
            })
            .orElse(null);
    }

    User toUser(UserPort.UserResponse userResponse);

}