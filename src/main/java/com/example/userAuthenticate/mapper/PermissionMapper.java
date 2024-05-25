package com.example.userAuthenticate.mapper;

import com.example.userAuthenticate.dto.request.PermissionRequest;
import com.example.userAuthenticate.dto.response.PermissionResponse;
import com.example.userAuthenticate.entity.Permission;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPerrmission(PermissionRequest request);
    PermissionResponse toPerrmissionResponse(Permission permission);

}
