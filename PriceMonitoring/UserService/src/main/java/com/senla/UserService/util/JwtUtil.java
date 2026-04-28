package com.senla.UserService.util;

import com.senla.UserService.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefrechSecret;

    public JwtUtil(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefrechSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(jwtAccessSecret.getBytes());
        this.jwtRefrechSecret = Keys.hmacShaKeyFor(jwtRefrechSecret.getBytes());
    }

    public String generateAccessToken(User user) {
        Instant expirationInstant = LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(expirationInstant);

        return Jwts.builder()
                .subject(user.getUsername())
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("userId", user.getId())
                .claim("role", user.getRole().getName())
                .claim("username", user.getUsername())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant expirationInstant = LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(expirationInstant);

        return Jwts.builder()
                .subject(user.getUsername())
                .expiration(refreshExpiration)
                .signWith(jwtRefrechSecret)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefrechSecret);
    }

    private boolean validateToken(String token, SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            // Логи
        } catch (UnsupportedJwtException unsEx) {
            // Логи
        } catch (MalformedJwtException mjEx) {
            // Логи
        } catch (Exception e) {
            // Логи
        }
        return false;
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefrechSecret);
    }

    private Claims getClaims(String token, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
