package com.msibai.cloud.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.msibai.cloud.validators.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({TYPE, ANNOTATION_TYPE})
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Retention(RUNTIME)
@Documented
public @interface PasswordMatches {
  String message() default "Passwords don't match";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
