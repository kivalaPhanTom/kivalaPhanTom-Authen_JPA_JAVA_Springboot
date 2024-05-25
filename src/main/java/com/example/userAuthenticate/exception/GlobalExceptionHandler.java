package com.example.userAuthenticate.exception;

import com.example.userAuthenticate.dto.request.ApiResponse;
import jakarta.persistence.Access;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Objects;

@Slf4j //dùng để log
// để can thiệp vào việc xử lý của các Controller thông thường.
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";
    @ExceptionHandler(value = RuntimeException.class) //bắt những lỗi runtime exception
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        log.info("Tại sao lỗi", exception);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class) //bắt những lỗi appexception
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumkey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumkey);
            var constrainViolation = exception.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);
            attributes = constrainViolation.getConstraintDescriptor().getAttributes();
        }catch (IllegalArgumentException e){

        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes) ? mapAttribute(errorCode.getMessage(), attributes) :errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    private String mapAttribute(String message, Map<String, Object> attributes){
        String minValue= String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
