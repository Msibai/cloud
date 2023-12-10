package com.msibai.cloud.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service for generating, extracting, and validating JSON Web Tokens (JWTs) used for
 * authentication.
 */
@Service
public class JwtService {

  // The secret key used for signing and verifying JWTs (Base64 encoded)
  static final String SECRET_KEY =
      "d833696eb18a299c37f1dd97839556a24607d9733807e83075d5ab938c44b147";

  /**
   * Generates a JWT using the UserDetails object and user ID.
   *
   * @param userDetails The UserDetails object representing user information.
   * @param userID The ID of the user for whom the token is generated.
   * @return The generated JWT token.
   */
  public String generateToken(UserDetails userDetails, UUID userID) {

    // Create extra claims (in this case, only the user ID)
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("userId", userID.toString());

    // Generate the token with the extra claims and UserDetails
    return generateToken(extraClaims, userDetails);
  }

  /**
   * Generates a JWT using the extra claims and UserDetails.
   *
   * @param extraClaims The additional claims to include in the JWT payload.
   * @param userDetails The UserDetails object representing user information.
   * @return The generated JWT token.
   */
  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extraClaims) // Set the extra claims in the JWT payload
        .setSubject(userDetails.getUsername()) // Set the subject (username) of the token
        .setIssuedAt(new Date(System.currentTimeMillis())) // Set the token issue time
        .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Set token expiration time
        .signWith(getSigningKey()) // Sign the token with the secret key
        .compact(); // Compact the JWT into its final string form
  }

  /**
   * Retrieves the signing key for JWT signing and validation.
   *
   * @return The signing key used for JWT operations.
   */
  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decode the base64 encoded secret key
    return Keys.hmacShaKeyFor(keyBytes); // Return the signing key for JWT operations
  }

  /**
   * Extracts the username from the JWT token.
   *
   * @param token The JWT token from which the username is to be extracted.
   * @return The extracted username.
   */
  public String extractUsername(String token) {
    return extractClaim(
        token, Claims::getSubject); // Extracts the subject (username) from the token's claims
  }

  /**
   * Extracts the user ID from the JWT token.
   *
   * @param token The JWT token from which the user ID is to be extracted.
   * @return The extracted user ID.
   */
  public String extractUserId(String token) {
    return extractClaim(
        token,
        claims ->
            claims.get(
                "userId", String.class)); // Extracts the "userId" claim from the token's claims
  }

  /**
   * Extracts a specific claim from the JWT token's claims using a given claimsResolver function.
   *
   * @param token The JWT token from which the claim is to be extracted.
   * @param claimsResolver The function that resolves the claim from the token's claims.
   * @param <T> The type of the claim being extracted.
   * @return The extracted claim.
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token); // Extracts all claims from the token
    return claimsResolver.apply(
        claims); // Resolves the specific claim using the claimsResolver function
  }

  /**
   * Extracts all claims from the JWT token's body.
   *
   * @param token The JWT token from which claims are to be extracted.
   * @return All claims extracted from the token.
   */
  private Claims extractAllClaims(String token) {
    String cleanToken = extractTokenFromHeader(token); // Extracts the clean token without "Bearer "
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey()) // Sets the signing key for parsing
        .build()
        .parseClaimsJws(cleanToken) // Parses the token to retrieve its body (claims)
        .getBody();
  }

  /**
   * Extracts the token from the authorization header.
   *
   * @param authHeader The authorization header containing the token.
   * @return The extracted token.
   */
  private String extractTokenFromHeader(String authHeader) {
    if (authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7); // Removes the "Bearer " prefix to get the token
    }
    return authHeader; // If the header doesn't start with "Bearer ", returns the header as it is
  }

  /**
   * Extracts the expiration date from the provided token.
   *
   * @param token The JWT token.
   * @return The expiration date extracted from the token.
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Checks if the provided token has expired.
   *
   * @param token The JWT token.
   * @return {@code true} if the token has expired, {@code false} otherwise.
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Validates if the provided token is valid for the given UserDetails.
   *
   * @param token The JWT token to validate.
   * @param userDetails The UserDetails object representing user information.
   * @return True if the token is valid for the provided user, otherwise false.
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {

    // Check if the token matches the username and is not expired
    return Objects.equals(extractUsername(token), userDetails.getUsername())
        && !isTokenExpired(token);
  }
}
