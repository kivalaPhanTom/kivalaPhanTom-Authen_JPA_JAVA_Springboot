package com.example.userAuthenticate.mapper;

import com.example.userAuthenticate.dto.request.UserCreationRequest;
import com.example.userAuthenticate.dto.request.UserUpdateRequest;
import com.example.userAuthenticate.dto.response.UserResponse;
import com.example.userAuthenticate.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    // sử dụng @MappingTarget để mapping "UserUpdateRequest request" tới "User user"
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget  User user, UserUpdateRequest request);
}
