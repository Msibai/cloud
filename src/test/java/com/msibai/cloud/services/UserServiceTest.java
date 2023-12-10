package com.msibai.cloud.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.entities.User;
import com.msibai.cloud.repositories.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock UserRepository userRepository;

  @InjectMocks UserService userService;

  @Test
  void testLoadUserByUserNameFound() {

    var mockUser = new User(UUID.randomUUID(), "first", "last", "test@test.com", "password", null);

    when(userRepository.findUserByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

    assertNotNull(userService.loadUserByUsername("test@test.com"));
    assertEquals(
        "test@test.com",
        userService.loadUserByUsername("test@test.com").getUsername());
    verify(userRepository, times(2)).findUserByEmail("test@test.com");
  }

  @Test
  void testLoadUserByUsernameNotFound() {

    when(userRepository.findUserByEmail("notFound@test.com")).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> userService.loadUserByUsername("notFound@test.com"));
    verify(userRepository).findUserByEmail("notFound@test.com");
  }

  @Test
  void testSaveUser() {

    var newUser = new User(UUID.randomUUID(), "first", "last", "test@test.com", "password", null);

    when(userRepository.save(newUser)).thenReturn(newUser);

    var savedUser = userService.save(newUser);

    assertNotNull(savedUser);
    assertEquals(savedUser.getEmail(), "test@test.com");
    verify(userRepository).save(newUser);
  }
}
