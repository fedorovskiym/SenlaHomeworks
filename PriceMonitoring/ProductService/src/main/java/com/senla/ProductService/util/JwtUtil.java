package com.senla.ProductService.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final SecretKey jwtAccessSecret;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(@Value("${jwt.secret.access}") String jwtAccessSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(jwtAccessSecret.getBytes());
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    private boolean validateToken(String token, SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            logger.error("Token expired: {}", expEx.getMessage());
        } catch (UnsupportedJwtException unsEx) {
            logger.error("Token unsupported: {}", unsEx.getMessage());
        } catch (MalformedJwtException mjEx) {
            logger.error("Token malformed: {}", mjEx.getMessage());
        } catch (Exception e) {
            logger.error("Invalid token: {}", e.getMessage());
        }
        return false;
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    private Claims getClaims(String token, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
