package com.msibai.cloud.filters;

import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith({MockitoExtension.class})
class JwtAuthenticationFilterTest {
  @Mock JwtService jwtService;

  @Mock UserDetailsService userDetailsService;

  @InjectMocks JwtAuthenticationFilter jwtAuthenticationFilter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);
  }

  @Test
  void doFilterInternalWithValidToken() throws ServletException, IOException {
    when(request.getHeader("Authentication")).thenReturn("Bearer validToken");
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
  void doFilterInternalWithInvalidToken() throws ServletException, IOException {
    when(request.getHeader("Authentication")).thenReturn("Bearer invalidToken");

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, times(1)).extractUsername("invalidToken");
    verify(jwtService, never()).isTokenValid(anyString(), any(UserDetails.class));
    verifyNoInteractions(userDetailsService);
  }

  @Test
  void doFilterInternalWithNoToken() throws ServletException, IOException {
    when(request.getHeader("Authentication")).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoInteractions(jwtService);
    verifyNoInteractions(userDetailsService);
  }
}
