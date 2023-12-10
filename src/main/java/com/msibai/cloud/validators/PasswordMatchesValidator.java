package com.msibai.cloud.validators;

import com.msibai.cloud.annotations.PasswordMatches;
import com.msibai.cloud.dtos.SignUpDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Validator class to check whether passwords in SignUpDto match each other. */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

  /**
   * Initializes the validator.
   *
   * @param constraintAnnotation The annotation instance being validated.
   */
  @Override
  public void initialize(PasswordMatches constraintAnnotation) {
    // Initialization not needed for this validator
  }

  /**
   * Validates whether the passwords in the SignUpDto match each other.
   *
   * @param o The object being validated (SignUpDto).
   * @param constraintValidatorContext The validation context.
   * @return True if passwords match, false otherwise.
   */
  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
    SignUpDto signUpDto = (SignUpDto) o;
    return signUpDto.getPassword().equals(signUpDto.getConfirmPassword());
  }
}
