package com.msibai.cloud.repositories;

import com.msibai.cloud.entities.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findUserByEmail(String email);
}
