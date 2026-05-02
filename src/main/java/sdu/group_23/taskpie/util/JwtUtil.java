package sdu.group_23.taskpie.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expire}")
    private long expire;

    private SecretKey key;
    private final Map<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Integer userId, Integer role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) return false;
        if (tokenBlacklist.containsKey(token)) return false;

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Integer getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get("userId");
        return Integer.parseInt(userId.toString());
    }

    public Integer getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        Object role = claims.get("role");
        return Integer.parseInt(role.toString());
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public void invalidateToken(String token) {
        try {
            Claims claims = parseToken(token);
            long expireTime = claims.getExpiration().getTime();
            tokenBlacklist.put(token, expireTime);
        } catch (Exception e) {}
    }

    public void cleanExpiredBlacklist() {
        long now = System.currentTimeMillis();
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}