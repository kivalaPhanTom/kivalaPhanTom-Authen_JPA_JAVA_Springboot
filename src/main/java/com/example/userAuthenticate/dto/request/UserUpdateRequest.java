package com.example.userAuthenticate.dto.request;

import com.example.userAuthenticate.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 3, message = "USERNAME_INVALID") //độ dài mật khẩu nhỏ nhất là 8 ký tự
    String username;

    @Size(min = 8, message = "INVALID_PASSWORD") //độ dài mật khẩu nhỏ nhất là 8 ký tự
    String password;
    String firstName;
    String lastName;

    @DobConstraint(min= 18, message = "INVALID_DOB")
    LocalDate dob;
    List<String> roles;
}
