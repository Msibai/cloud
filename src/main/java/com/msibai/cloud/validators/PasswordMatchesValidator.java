package com.msibai.cloud.validators;

import com.msibai.cloud.annotations.PasswordMatches;
import com.msibai.cloud.dtos.SignUpDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

  @Override
  public void initialize(PasswordMatches constraintAnnotation) {}

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

    SignUpDto signUpDto = (SignUpDto) o;
    return signUpDto.getPassword().equals(signUpDto.getConfirmPassword());
  }
}
