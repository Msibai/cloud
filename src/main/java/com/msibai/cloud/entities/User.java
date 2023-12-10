package com.msibai.cloud.entities;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Entity class representing a user in the system. */
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id; // Unique identifier for the user

  @NonNull private String firstName; // First name of the user

  @NonNull private String lastName; // Last name of the user

  @NonNull
  @Column(unique = true)
  private String email; // Email address of the user (unique)

  @NonNull private String encodedPassword; // Encoded password for authentication

  @Enumerated(EnumType.STRING)
  private Role role; // Role of the user (admin or regular user)

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Providing user authorities based on their assigned role
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getPassword() {
    // Retrieving the encoded password for authentication
    return encodedPassword;
  }

  @Override
  public String getUsername() {
    // Retrieving the email as the username for authentication
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    // Indicating that the user account never expires
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // Indicating that the user account is never locked
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // Indicating that the user credentials never expire
    return true;
  }

  @Override
  public boolean isEnabled() {
    // Indicating that the user account is always enabled
    return true;
  }
}
