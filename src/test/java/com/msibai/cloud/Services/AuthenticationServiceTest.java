package com.msibai.cloud.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignInDto;
import com.msibai.cloud.dtos.SignUpDto;
import com.msibai.cloud.entities.Role;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.repositories.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
  @Mock UserService userService;
  @Mock JwtService jwtService;
  @Mock PasswordEncoder passwordEncoder;
  @Mock UserRepository userRepository;
  @Mock AuthenticationManager authenticationManager;
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
            .role(Role.ROLE_USER)
            .build();
    when(userService.save(any(User.class))).thenReturn(mockUser);
    when(jwtService.generateToken(any(User.class))).thenReturn("mockedJwtToken");

    JwtAuthenticationResponseDto response = authenticationService.signUp(signUpDto);

    assertEquals("mockedJwtToken", response.getToken());
    verify(userService, times(1)).save(any(User.class));
  }

  @Test
  void testSignInWithValidUsernameAndPassword() {
    SignInDto signInDto = new SignInDto("test@test.com", "password");
    User mockUser = new User(UUID.randomUUID(), "first", "last", "test@test.com", "password", null);

    when(userRepository.findUserByEmail(signInDto.getEmail())).thenReturn(Optional.of(mockUser));

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword());

    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
    when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mockedJwtToken");

    JwtAuthenticationResponseDto response = authenticationService.signIn(signInDto);

    assertEquals("mockedJwtToken", response.getToken());
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userRepository, times(1)).findUserByEmail(signInDto.getEmail());
  }

  @Test
  void testSignInWithInvalidUsername() {
    SignInDto signInDto = new SignInDto("invalidEmail@test.com", "password");

    when(userRepository.findUserByEmail(signInDto.getEmail()))
        .thenThrow(UsernameNotFoundException.class);

    assertThrows(UsernameNotFoundException.class, () -> authenticationService.signIn(signInDto));
    verify(userRepository, times(1)).findUserByEmail(anyString());
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void testSignInWithInvalidPassword() {
    SignInDto signInDto = new SignInDto("validEmail@test.com", "invalidPassword");

    doThrow(IllegalArgumentException.class)
        .when(authenticationManager)
        .authenticate(any(UsernamePasswordAuthenticationToken.class));

    assertThrows(IllegalArgumentException.class, () -> authenticationService.signIn(signInDto));
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
  }
}
