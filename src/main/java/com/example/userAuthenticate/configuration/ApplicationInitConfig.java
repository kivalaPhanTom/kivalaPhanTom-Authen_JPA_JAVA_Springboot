package com.example.userAuthenticate.configuration;

import com.example.userAuthenticate.constant.PredefinedRole;
import com.example.userAuthenticate.entity.Role;
import com.example.userAuthenticate.entity.User;
import com.example.userAuthenticate.repository.RoleRepository;
import com.example.userAuthenticate.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor // sẽ sinh ra một constructor với các tham số bắt buộc phải có giá trị.
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j //dùng để log

public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";
    RoleRepository roleRepository;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
           if(userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()){ //nếu chưa có user admin
               Role adminRole = roleRepository.save(Role.builder()
                       .name(PredefinedRole.ADMIN_ROLE)
                       .description("Admin role")
                       .build());

               var roles = new HashSet<Role>();
               roles.add(adminRole);
               User user = User.builder()
                       .username(ADMIN_USER_NAME)
                       .roles(roles)
                       .password(passwordEncoder.encode(ADMIN_PASSWORD))
                       .build();
               userRepository.save(user);
               log.warn("admin user has been created with default password: admin, please change it");
           }
        };
    }
}
