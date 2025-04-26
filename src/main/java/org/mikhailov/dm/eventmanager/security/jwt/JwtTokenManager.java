package org.mikhailov.dm.eventmanager.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.mikhailov.dm.eventmanager.users.User;
import org.mikhailov.dm.eventmanager.users.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenManager {

    private final UserService userService;
    private final SecretKey key;
    private final long expirationTime;

    public JwtTokenManager(@Lazy UserService userService,
                           @Value("${jwt.secretkey}") String keyString,
                           @Value("${jwt.lifetime}") long expirationTime) {
        this.userService = userService;
        this.key = Keys.hmacShaKeyFor(keyString.getBytes((StandardCharsets.UTF_8)));
        this.expirationTime = expirationTime;
    }

    public String generateToken(String login) {
        User user = userService.findByLogin(login);

        return Jwts
                .builder()
                .subject(login)
                .claim("userId", user.id())
                .claim("userRole", user.role().name())
                .signWith(key)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();
    }

    public String getLoginFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userRole", String.class);
    }
}
