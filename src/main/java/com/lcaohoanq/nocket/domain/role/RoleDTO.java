package com.lcaohoanq.nocket.domain.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lcaohoanq.nocket.enums.UserRole;
import lombok.Builder;

@Builder
public record RoleDTO(
    @JsonProperty("name")
    UserRole userRole
) {}
