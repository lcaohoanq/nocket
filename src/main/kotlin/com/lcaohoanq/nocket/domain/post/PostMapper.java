package com.lcaohoanq.nocket.domain.post;

import com.lcaohoanq.nocket.domain.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class}
)
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "postOwner", source = "user")
    PostPort.PostResponse toPostResponse(Post post);
}
