package com.example.userAuthenticate.dto.request;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) //dùng khi ko muốn trả ra field nào có value = null
public class ApiResponse<T> {
     @Builder.Default
     int code = 1000;
     String message;
     T result;
}
