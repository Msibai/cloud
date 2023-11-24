package com.msibai.cloud.filters;

import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ExtendWith({MockitoExtension.class})
class JwtAuthenticationFilterTest {
  @Mock JwtService jwtService;
  @Mock UserDetailsService userDetailsService;
  @Mock HandlerExceptionResolver exceptionResolver;
  private JwtAuthenticationFilter jwtAuthenticationFilter;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  @BeforeEach
  void setUp() throws IllegalAccessException, NoSuchFieldException {
    jwtAuthenticationFilter = new JwtAuthenticationFilter(exceptionResolver);

    Field jwtServiceField = JwtAuthenticationFilter.class.getDeclaredField("jwtService");
    jwtServiceField.setAccessible(true);
    jwtServiceField.set(jwtAuthenticationFilter, jwtService);

    Field userDetailsServiceField =
        JwtAuthenticationFilter.class.getDeclaredField("userDetailsService");
    userDetailsServiceField.setAccessible(true);
    userDetailsServiceField.set(jwtAuthenticationFilter, userDetailsService);
  }

  @Test
  void testDoFilterInternalWithValidToken() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
    when(jwtService.extractUsername("validToken")).thenReturn("testUser@test.com");
    when(userDetailsService.loadUserByUsername("testUser@test.com"))
        .thenReturn(mock(UserDetails.class));
    when(jwtService.isTokenValid(eq("validToken"), any(UserDetails.class))).thenReturn(true);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, times(1)).extractUsername("validToken");
    verify(jwtService, times(1)).isTokenValid(eq("validToken"), any(UserDetails.class));
    verify(userDetailsService, times(1)).loadUserByUsername("testUser@test.com");
  }

  @Test
  void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, times(1)).extractUsername("invalidToken");
    verify(jwtService, never()).isTokenValid(anyString(), any(UserDetails.class));
    verifyNoInteractions(userDetailsService);
  }

  @Test
  void testDoFilterInternalWithNoToken() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoInteractions(jwtService);
    verifyNoInteractions(userDetailsService);
  }
}
