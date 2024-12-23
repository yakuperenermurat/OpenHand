package com.openhand.openhand.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtTokenService {

    private final String secretKey = "your-256-bit-secret"; // Güçlü bir secret key kullan
    private Set<String> tokenBlacklist = new HashSet<>();

    //Gelen token'ı doğrular.
    public boolean validateToken(String token) {
        if (tokenBlacklist.contains(token)) {
            return false; // Token geçersiz
        }

        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //token'ı kara listeye ekler
    public void invalidateToken(String token) {
        tokenBlacklist.add(token); // Token kara listeye alındı
    }

    //15 dakika geçerli bir access token oluşturur
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 dakika geçerli
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    //7 gün geçerli bir "refresh token" oluşturur.
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 gün geçerli
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    //Refresh token'ı doğrular
    public boolean validateRefreshToken(String token) {
        if (tokenBlacklist.contains(token)) {
            return false; // Token geçersiz
        }
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // Token'dan email bilgisi çıkarır.
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // "setSubject" ile eklediğimiz email'i döner
    }
}
