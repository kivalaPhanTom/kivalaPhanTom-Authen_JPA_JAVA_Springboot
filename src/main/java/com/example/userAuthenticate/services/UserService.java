package com.example.userAuthenticate.services;

import com.example.userAuthenticate.constant.PredefinedRole;
import com.example.userAuthenticate.dto.request.UserCreationRequest;
import com.example.userAuthenticate.dto.request.UserUpdateRequest;
import com.example.userAuthenticate.dto.response.UserResponse;
import com.example.userAuthenticate.entity.Role;
import com.example.userAuthenticate.entity.User;
import com.example.userAuthenticate.exception.AppException;
import com.example.userAuthenticate.exception.ErrorCode;
import com.example.userAuthenticate.mapper.UserMapper;
import com.example.userAuthenticate.repository.RoleRepository;
import com.example.userAuthenticate.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.List;

@Slf4j //dùng để log
@Service
@RequiredArgsConstructor // sẽ sinh ra một constructor với các tham số bắt buộc phải có giá trị.
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//nếu các field bên trong class ko xac định phạm vi, thì sẽ mặc định là private hết,đồng thời, field đó có kiểu là final

public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
        return userRepository.save(user);
    }
    //@PreAuthorize("hasAuthority('GET_DATA')") //nếu có permission là GET_DATA thì mới cho chạy vô hàm này
    @PreAuthorize("hasRole('ADMIN')") //kiểm tra phải có role Admin mới cho gọi hàm (tóm lại, kiểm tra điều kiện trước, thỏa mới cho vào method)
    public List<UserResponse> getUsers(){
        log.info("In method get Users");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    //mục đích của hàm này là chỉ lấy ra đúng user của chính mình
    //"returnObject.username == authentication.name" là điều kiện truyền vào, returnObject là kết quả trả ra
    // @PostAuthorize("returnObject.username == authentication.name") //vẫn cho gọi vào hàm, tuy nhiên, nếu ko phải là user admin thì ko trả ra (tóm lại cho vào method trước, sau đó mới kiểm tra điều kiện, ko thỏa thì chặn lại)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new RuntimeException(("User not found")))
        );
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(("User not found")));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PostAuthorize("returnObject.username == authentication.name") //vẫn cho gọi vào hàm, tuy nhiên, nếu ko phải là user admin thì ko trả ra (tóm lại cho vào method trước, sau đó mới kiểm tra điều kiện, ko thỏa thì chặn lại)
    public UserResponse getMyinfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

}
