package com.msibai.cloud.config;

import com.msibai.cloud.Services.UserService;
import com.msibai.cloud.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/** Configuration class for setting up security configurations. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private final UserService userService;
  private final HandlerExceptionResolver exceptionResolver;

  /**
   * Constructor to initialize UserService and HandlerExceptionResolver.
   *
   * @param userService UserService instance.
   * @param exceptionResolver HandlerExceptionResolver instance.
   */
  public SecurityConfig(
      UserService userService,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
    this.userService = userService;
    this.exceptionResolver = exceptionResolver;
  }

  /**
   * Configures the security filter chain.
   *
   * @param http HttpSecurity object for configuring security.
   * @return SecurityFilterChain instance.
   * @throws Exception If an error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/api/v1/signup", "/api/v1/login")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  /**
   * Creates an AuthenticationProvider bean for authentication.
   *
   * @return AuthenticationProvider instance.
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  /**
   * Creates a JwtAuthenticationFilter bean for processing JWT authentication.
   *
   * @return JwtAuthenticationFilter instance.
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(exceptionResolver);
  }

  /**
   * Creates a PasswordEncoder bean for encoding passwords.
   *
   * @return PasswordEncoder instance.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Creates an AuthenticationManager bean for authentication purposes.
   *
   * @param config AuthenticationConfiguration for obtaining the AuthenticationManager.
   * @return AuthenticationManager instance.
   * @throws Exception If an error occurs while obtaining the AuthenticationManager.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
