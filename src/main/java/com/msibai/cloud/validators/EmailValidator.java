package com.msibai.cloud.validators;

import com.msibai.cloud.annotations.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Validator class to check the validity of an email address using regex pattern matching. */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
  // Regular expression pattern for validating an email address
  private static final String EMAIL_PATTERN =
      "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

  /**
   * Initializes the validator.
   *
   * @param constraintAnnotation The annotation instance being validated.
   */
  @Override
  public void initialize(ValidEmail constraintAnnotation) {
    // Initialization not needed for this validator
  }

  /**
   * Validates whether the given email address matches the specified pattern.
   *
   * @param email The email address to be validated.
   * @param context The validation context.
   * @return True if the email is valid, false otherwise.
   */
  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    return validateEmail(email);
  }

  /**
   * Validates the email against the specified regular expression pattern.
   *
   * @param email The email address to be validated.
   * @return True if the email matches the pattern, false otherwise.
   */
  private boolean validateEmail(String email) {
    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }
}
