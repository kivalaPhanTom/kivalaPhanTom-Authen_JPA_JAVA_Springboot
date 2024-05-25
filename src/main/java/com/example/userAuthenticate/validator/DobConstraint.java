package com.example.userAuthenticate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
//đây là 1 custom annotation
@Target({ElementType.FIELD}) //đối tượng cần validate là các field trong object
@Retention(RetentionPolicy.RUNTIME) //thời điểm annotation được xử lý lúc nào, ở đây là chọn xử lý lúc runtime
@Constraint(
        validatedBy = {DobValidator.class}
)
public @interface DobConstraint {
    String message() default "Invalide date of birth";
    int min();
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
