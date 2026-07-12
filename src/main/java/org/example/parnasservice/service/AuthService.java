package org.example.parnasservice.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.example.parnasservice.dto.request.LoginRequest;
import org.example.parnasservice.dto.request.LogoutRequest;
import org.example.parnasservice.dto.request.RefreshTokenRequest;
import org.example.parnasservice.dto.request.RegisterRequest;
import org.example.parnasservice.dto.response.AuthResponse;
import org.example.parnasservice.dto.response.TokenPair;
import org.example.parnasservice.dto.response.UserProfileResponse;
import org.example.parnasservice.entity.RefreshToken;
import org.example.parnasservice.entity.User;
import org.example.parnasservice.exception.ConflictException;
import org.example.parnasservice.exception.ResourceNotFoundException;
import org.example.parnasservice.mapper.EntityMapper;
import org.example.parnasservice.repository.RefreshTokenRepository;
import org.example.parnasservice.repository.UserRepository;
import org.example.parnasservice.security.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final EntityMapper entityMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByWalletAddress(request.getWalletAddress())) {
            throw new ConflictException("WALLET_ALREADY_REGISTERED",
                "Адрес кошелька уже зарегистрирован. Выполните вход.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("USERNAME_TAKEN",
                "Username уже занят. Выберите другой.");
        }

        User user = new User(
            UUID.randomUUID(),
            request.getWalletAddress(),
            request.getUsername(),
            request.getFirstName(),
            request.getLastName(),
            Instant.now(),
            null
        );
        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByWalletAddress(request.getWalletAddress())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Адрес кошелька не найден. Пройдите регистрацию."));

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional
    public TokenPair refresh(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh token не найден"));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.revokeAllByUserId(storedToken.getUser().getId());
            throw new ConflictException("TOKEN_REVOKED",
                "Refresh token отозван или истёк. Все токены сессии аннулированы.");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getWalletAddress());
        String newRefreshToken = jwtProvider.generateRefreshToken();
        saveRefreshToken(user, newRefreshToken);

        return new TokenPair(accessToken, newRefreshToken, "Bearer", jwtProvider.getRefreshTokenExpirationSeconds());
    }

    @Transactional
    public void logout(LogoutRequest request, UUID currentUserId) {
        if (request.isAllSessions()) {
            refreshTokenRepository.revokeAllByUserId(currentUserId);
        } else if (request.getRefreshToken() != null) {
            String tokenHash = hashToken(request.getRefreshToken());
            refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(t -> {
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                });
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getWalletAddress());
        String refreshTokenStr = jwtProvider.generateRefreshToken();
        saveRefreshToken(user, refreshTokenStr);

        TokenPair tokens = new TokenPair(accessToken, refreshTokenStr, "Bearer",
            jwtProvider.getRefreshTokenExpirationSeconds());

        UserProfileResponse profile = entityMapper.toUserProfile(user);
        return new AuthResponse(profile, tokens);
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken rt = new RefreshToken();
        rt.setId(UUID.randomUUID());
        rt.setUser(user);
        rt.setTokenHash(hashToken(token));
        rt.setExpiresAt(Instant.now().plusSeconds(jwtProvider.getRefreshTokenExpirationSeconds()));
        rt.setRevoked(false);
        rt.setCreatedAt(Instant.now());
        refreshTokenRepository.save(rt);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
