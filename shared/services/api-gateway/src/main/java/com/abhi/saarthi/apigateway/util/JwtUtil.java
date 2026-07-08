package com.abhi.saarthi.apigateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration:900000}") // default 15 minutes
    private long accessTokenExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Validates token and returns all claims if valid
     * Throws JwtException on any validation error
     */
    public Claims validateAndGetClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new MalformedJwtException("Token is null or empty");
        }

        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token has expired");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("Unsupported JWT format");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Invalid JWT format");
        } catch (SignatureException e) {
            throw new SignatureException("Invalid JWT signature");
        } catch (Exception e) {
            throw new JwtException("Token validation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts subject (username) without full validation
     * Useful for logging / error messages
     */
    public String getUsernameFromToken(String token) {
        try {
            return validateAndGetClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Optional: Generate new access token (used in Auth Service)
     */
    public String generateAccessToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                // You can add more claims here if needed
                // .claim("roles", roles)
                // .claim("jti", UUID.randomUUID().toString()) // for revocation support
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Check if token is expired (without throwing exception)
     * Useful for some edge cases
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateAndGetClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false; // invalid token → treat as expired
        }
    }
}
