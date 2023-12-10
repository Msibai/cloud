package com.msibai.cloud.repositories;

import com.msibai.cloud.entities.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository interface for managing User entities. */
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * Finds a user by email.
   *
   * @param email The email of the user.
   * @return An Optional containing the user object if found, else empty.
   */
  Optional<User> findUserByEmail(String email);
}
