package com.msibai.cloud.Services;

import com.msibai.cloud.entities.User;
import com.msibai.cloud.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findUserByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found!"));
  }

  public User save(User newUser) {
    return userRepository.save(newUser);
  }
}
