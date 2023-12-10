package com.msibai.cloud.annotations;

import com.msibai.cloud.validators.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/** Custom annotation to validate an email address using the EmailValidator class. */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {

  /**
   * Specifies the default error message when the validation fails.
   *
   * @return The error message.
   */
  String message() default "Invalid email";

  /**
   * Specifies the validation groups this constraint belongs to. Groups allow constraints to be
   * targeted for specific validation scenarios.
   *
   * @return The validation groups.
   */
  Class<?>[] groups() default {};

  /**
   * Provides a means to associate additional metadata with a validation constraint.
   *
   * @return The payload.
   */
  Class<? extends Payload>[] payload() default {};
}
