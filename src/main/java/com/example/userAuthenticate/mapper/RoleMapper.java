package com.example.userAuthenticate.mapper;

import com.example.userAuthenticate.dto.request.RoleRequest;
import com.example.userAuthenticate.dto.response.RoleResponse;
import com.example.userAuthenticate.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true) //trong quá trình mapping thì bỏ qua field permission
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
