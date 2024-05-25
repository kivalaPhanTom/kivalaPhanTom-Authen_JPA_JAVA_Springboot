package com.example.userAuthenticate.controller;

import com.example.userAuthenticate.dto.request.ApiResponse;
import com.example.userAuthenticate.dto.request.UserCreationRequest;
import com.example.userAuthenticate.dto.request.UserUpdateRequest;
import com.example.userAuthenticate.dto.response.UserResponse;
import com.example.userAuthenticate.entity.User;
import com.example.userAuthenticate.exception.AppException;
import com.example.userAuthenticate.exception.ErrorCode;
import com.example.userAuthenticate.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j //dùng để log

public class UserController {
    UserService userService;

    //do dùng thư viện để validate nên có sử dụng @Valid, nếu ko validate thì có thể bỏ @Valid
    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication(); //SecurityContextHolder chứa thong tin user đang đăng nhập hiện tại
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        log.info("qua đc xác thực");
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @GetMapping("/myinfo")
    ApiResponse<UserResponse> getUser(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyinfo())
                .build();
    }
}
