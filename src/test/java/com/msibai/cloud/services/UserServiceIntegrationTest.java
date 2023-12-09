package com.msibai.cloud.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.msibai.cloud.entities.Role;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceIntegrationTest {
  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.1");

  @Autowired UserRepository userRepository;

  @Test
  void testUserSaveAndRetrieve() {

    var user = new User();
    user.setFirstName("first");
    user.setLastName("last");
    user.setEmail("test@test.com");
    user.setEncodedPassword("password");
    user.setRole(Role.ROLE_USER);

    userRepository.save(user);

    Optional<User> retrievedUser = userRepository.findUserByEmail(user.getEmail());

    assertThat(retrievedUser).isPresent();
    assertThat(retrievedUser.get().getId()).isNotNull();
    assertThat(retrievedUser.get().getFirstName()).isEqualTo("first");
  }
}
