package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.cart.Cart;
import com.lcaohoanq.nocket.domain.cart.CartResponse;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CartMapper {

    @Mapping(source = "user", target = "user", qualifiedByName = "mapUserToUserResponse")
    CartResponse toCartResponse(Cart cart);

    @Named("mapUserToUserResponse")
    default UserResponse mapUser(User user) {
        return user == null ? null : Mappers.getMapper(UserMapper.class).toUserResponse(user);
    }

}