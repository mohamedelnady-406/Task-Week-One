package com.example.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Singleton
public class JwtService {
    /*
    * should be stored in ENV_Variable for production
    */
    private final Key key = Keys.hmacShaKeyFor(
            "8996aabdf530e5281bcdf93460f72692dfdab01f08e35ab31661691d6fd29d58".getBytes()
    );
    public String generateToken(String username, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .claims(extraClaims)
                .expiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }
}
