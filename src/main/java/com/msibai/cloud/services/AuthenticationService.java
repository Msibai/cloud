package com.msibai.cloud.services;

import com.msibai.cloud.services.impl.FolderServiceImpl;
import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignInDto;
import com.msibai.cloud.dtos.SignUpDto;
import com.msibai.cloud.entities.Role;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Service responsible for user authentication operations such as sign-up and sign-in. */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserService userService; // UserService for handling user-related operations
  private final PasswordEncoder passwordEncoder; // Encoder for password hashing
  private final JwtService jwtService; // Service for JWT token management
  private final UserRepository userRepository; // Repository for User entities
  private final AuthenticationManager authenticationManager; // Spring's AuthenticationManager
  private final FolderServiceImpl folderServiceImpl; // Service for folder-related operations

  // Method for user registration (sign-up)
  public JwtAuthenticationResponseDto signUp(SignUpDto signUpDto) {

    // Check if email already exists in the database
    if (isEmailExists(signUpDto.getEmail())) {
      throw new DuplicateKeyException(
          "There is an account with that email address: " + signUpDto.getEmail());
    }

    // Create a new User entity from the sign-up DTO data
    var user =
        User.builder()
            .firstName(signUpDto.getFirstName())
            .lastName(signUpDto.getLastName())
            .email(signUpDto.getEmail())
            .encodedPassword(passwordEncoder.encode(signUpDto.getPassword())) // Hash the password
            .role(Role.ROLE_USER) // Set the role as ROLE_USER
            .build();

    // Save the new user to the database
    user = userService.save(user);
    var userId = user.getId();

    // Create a root folder for the newly registered user
    folderServiceImpl.createRootFolderForNewUser(userId);

    // Generate a JWT token for the user
    var jwt = jwtService.generateToken(user, userId);

    return JwtAuthenticationResponseDto.builder().token(jwt).build();
  }

  // Check if an email already exists in the database
  private boolean isEmailExists(String email) {
    return userRepository.findUserByEmail(email).isPresent();
  }

  // Method for user authentication (sign-in)
  public JwtAuthenticationResponseDto signIn(SignInDto signInDto) {

    // Authenticate user credentials using Spring's AuthenticationManager
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword()));

    // Retrieve user details from the database
    var user =
        userRepository
            .findUserByEmail(signInDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
    var userId = user.getId();

    // Generate a JWT token for the authenticated user
    var jwt = jwtService.generateToken(user, userId);

    return JwtAuthenticationResponseDto.builder().token(jwt).build();
  }
}
