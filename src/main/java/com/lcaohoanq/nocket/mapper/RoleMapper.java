package com.lcaohoanq.nocket.mapper;

import com.lcaohoanq.nocket.domain.role.Role;
import com.lcaohoanq.nocket.domain.role.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "name", source = "userRole")
    RoleResponse toRoleResponse(Role role);
}

