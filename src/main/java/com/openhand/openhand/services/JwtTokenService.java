package com.openhand.openhand.services;

import com.openhand.openhand.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtTokenService {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Güvenli bir secret key oluşturulur
    private final Set<String> tokenBlacklist = new HashSet<>();

    // Gelen token'ı doğrular
    public boolean validateToken(String token) {
        if (tokenBlacklist.contains(token)) {
            return false; // Token geçersiz
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Token'ı kara listeye ekler
    public void invalidateToken(String token) {
        tokenBlacklist.add(token); // Token kara listeye alındı
    }

    // 15 dakika geçerli bir access token oluşturur
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 7 gün geçerli bir "refresh token" oluşturur
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 gün geçerli
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh token'ı doğrular
    public boolean validateRefreshToken(String token) {
        if (tokenBlacklist.contains(token)) {
            return false; // Token geçersiz
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    // Token'dan email bilgisi çıkarır
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // "setSubject" ile eklediğimiz email'i döner
    }

    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class); // "role" claim'inden rolü al
    }
}
