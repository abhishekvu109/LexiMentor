package com.abhi.saarthi.auth.service;

import com.abhi.saarthi.auth.entity.RefreshToken;
import com.abhi.saarthi.auth.entity.User;
import com.abhi.saarthi.auth.repository.RefreshTokenRepository;
import com.abhi.saarthi.auth.repository.UserRepository;
import com.abhi.saarthi.auth.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RefreshTokenService {

    @Value("${jwt.refresh.token.expiration.ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));
        RefreshToken token = user.getRefreshToken();
        if (token == null) {
            token = RefreshToken.builder()
                    .user(user)
                    .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                    .token(KeyGeneratorUtil.uuid())
                    .build();
            return refreshTokenRepository.save(token);
        } else {

            if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
                token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
                token.setToken(KeyGeneratorUtil.uuid());
                token = refreshTokenRepository.save(token);
            }
            return token;
        }

    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new sign in request");
        }
        return token;
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresentOrElse(refreshTokenRepository::delete, () -> {
            throw new RuntimeException("Refresh token is not in database!");
        });
    }
}
