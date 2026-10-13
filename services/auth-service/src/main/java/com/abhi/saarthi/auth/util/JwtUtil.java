package com.abhi.saarthi.auth.util;

import com.abhi.saarthi.auth.constant.ApplicationConstants;
import com.abhi.saarthi.auth.entity.User;
import com.abhi.saarthi.auth.entity.UserRole;
import com.abhi.saarthi.auth.repository.UserRepository;
import com.abhi.saarthi.auth.repository.UserRoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.token.expiration.ms}")
    private Long jwtTokenExpirationMs;

    private Key key;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User doesn't exist."));
        Map<String, Object> claims = new HashMap<>();
        Set<UserRole> userRoles = this.getRolesByUsername(username);
        if (CollectionUtils.isEmpty(userRoles)) {
            throw new RuntimeException("User roles are not found.");
        }
        claims.put("roles", userRoles.stream().map(UserRole::getName).collect(Collectors.toList()));
        claims.put("profile", user.getProfileType().name());
        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtTokenExpirationMs)) // 1 hour
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Set<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);

        Object rolesObj = claims.get("roles");

        if (rolesObj instanceof Set<?>) {
            @SuppressWarnings("unchecked")
            Set<String> rolesList = (Set<String>) rolesObj;
            return new HashSet<>(rolesList);
        }

        return Set.of();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private Set<UserRole> getRolesByUsername(String username) {
        Set<UserRole> userRoles = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User doesn't exist.")).getRoles();
        if (CollectionUtils.isEmpty(userRoles)) {
            userRoles.addAll(ApplicationConstants.ROLES.stream().map(role -> userRoleRepository.findByNameIgnoreCase(role).orElseThrow(() -> new RuntimeException("Role not found"))).collect(Collectors.toSet()));
        }
        return userRoles;
    }

}
