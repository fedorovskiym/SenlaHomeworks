package com.senla.task1.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret.key}")
    private String key;

    public String generateToken(String username) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(30).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(key));
    }

    public DecodedJWT validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key))
                .withSubject("User details")
                .build();
        return verifier.verify(token);
    }

    public String getUsername(String token) {
        return validateToken(token).getClaim("username").asString();
    }
}
