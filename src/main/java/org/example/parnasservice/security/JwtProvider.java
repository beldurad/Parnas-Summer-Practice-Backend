package org.example.parnasservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final int accessTokenExpiration;
    private final int refreshTokenExpiration;

    public JwtProvider(@Value("${parnas.jwt.secret}") String secret,
                       @Value("${parnas.jwt.access-token-expiration}") int accessTokenExpiration,
                       @Value("${parnas.jwt.refresh-token-expiration}") int refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(UUID userId, String walletAddress) {
        return Jwts.builder()
            .subject(userId.toString())
            .claim("wallet", walletAddress)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000L))
            .signWith(secretKey)
            .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

    public int getRefreshTokenExpirationSeconds() {
        return refreshTokenExpiration;
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(parseToken(token).getSubject());
    }
}
