package com.msibai.cloud.filters;

import com.msibai.cloud.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/** Custom JWT authentication filter to process JWT tokens sent in the Authorization header. */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final HandlerExceptionResolver exceptionResolver;

  @Autowired private JwtService jwtService;
  @Autowired private UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(HandlerExceptionResolver exceptionResolver) {

    this.exceptionResolver = exceptionResolver;
  }

  /**
   * Performs JWT authentication for each incoming request.
   *
   * @param request HTTP request object.
   * @param response HTTP response object.
   * @param filterChain Filter chain for request processing.
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;

    try {
      // Check if the Authorization header is present and starts with "Bearer "
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      // Extract JWT token
      jwt = authHeader.substring(7);
      userEmail = jwtService.extractUsername(jwt);

      // Check if the token is valid and set the authentication in SecurityContextHolder
      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (jwtService.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
      // Continue with the filter chain
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      // Resolve and handle exceptions
      exceptionResolver.resolveException(request, response, null, ex);
    }
  }
}
