package com.msibai.cloud.services;

import com.msibai.cloud.entities.User;
import com.msibai.cloud.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Service for managing User-related operations and authentication. */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Loads user details by username (in this case, by email) for authentication.
   *
   * @param username The email address of the user.
   * @return UserDetails object containing user information.
   * @throws UsernameNotFoundException If the user is not found.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    // Queries the UserRepository to find a user by the provided email
    return userRepository
        .findUserByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found!"));
  }

  /**
   * Saves a new user to the database.
   *
   * @param newUser The User entity representing the new user.
   * @return The saved User entity.
   */
  public User save(User newUser) {
    return userRepository.save(newUser);
  }
}
