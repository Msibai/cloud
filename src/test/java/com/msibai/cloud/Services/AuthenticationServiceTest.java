package com.msibai.cloud.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignUpDto;
import com.msibai.cloud.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
  @Mock UserService userService;
  @Mock JwtService jwtService;
  @Mock PasswordEncoder passwordEncoder;
  @InjectMocks AuthenticationService authenticationService;

  @Test
  void testSignUp() {

    SignUpDto signUpDto =
        new SignUpDto("first name", "last name", "test@test.com", "password", "password");

    when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
    User mockUser =
        User.builder()
            .firstName(signUpDto.getFirstName())
            .lastName(signUpDto.getLastName())
            .email(signUpDto.getEmail())
            .encodedPassword("encodedPassword")
            .build();
    when(userService.save(any(User.class))).thenReturn(mockUser);
    when(jwtService.generateToken(any(User.class))).thenReturn("mockedJwtToken");

    JwtAuthenticationResponseDto response = authenticationService.signUp(signUpDto);

    assertEquals("mockedJwtToken", response.getToken());
    verify(userService, times(1)).save(any(User.class));
  }
}
