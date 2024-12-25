// JwtAuthenticationFilter.java
package com.openhand.openhand.config;

import com.openhand.openhand.entities.User;
import com.openhand.openhand.services.AdminService;
import com.openhand.openhand.services.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// Tüm isteklerde Access Token doğrulaması yapar.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtService;

    @Autowired
    private AdminService adminService; // Kullanıcı doğrulama için

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " kısmını çıkar

            // Token geçerli mi?
            if (jwtService.validateToken(token)) {
                try {
                    String email = jwtService.getEmailFromToken(token);
                    String role = jwtService.getRoleFromToken(token);

                    if (!StringUtils.hasText(role)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\": \"Yetki bilgisi eksik.\"}");
                        return; // Filtre zincirini sonlandır
                    }

                    // Kullanıcıyı al ve aktiflik durumunu kontrol et
                    User user = adminService.findUserByEmail(email);

                    if (!user.isActive()) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\": \"Hesabınız askıya alınmış.\"}");
                        return; // Filtre zincirini sonlandır
                    }

                    // Kullanıcı aktifse doğrulama bilgilerini ayarla
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (IllegalArgumentException e) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
                    return; // Filtre zincirini sonlandır
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Geçersiz veya süresi dolmus token.\"}");
                return; // Filtre zincirini sonlandır
            }
        }
        // Filtre zincirine devam et
        filterChain.doFilter(request, response);
    }
}
