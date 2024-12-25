package com.openhand.openhand.services;

import com.openhand.openhand.entities.User;
import com.openhand.openhand.enums.Role;
import com.openhand.openhand.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenService jwtService;

    // Email ile kullanıcı bul
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı e-posta adresi bulunamadı: " + email));
    }

    // Kullanıcıyı Askıya Alma
    public void suspendUser(Long adminId, Long userId, LocalDateTime until) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin bulunamadı!"));
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("Bu işlem sadece adminler tarafından yapılabilir.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        user.setSuspendedUntil(until);

        // Kullanıcının tüm tokenlarını kara listeye ekle
        jwtService.invalidateToken(jwtService.generateAccessToken(user)); // Mevcut token'ı geçersiz yap
        jwtService.invalidateToken(jwtService.generateRefreshToken(user));

        userRepository.save(user);
    }

    // Tüm Kullanıcıları Listele
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Kullanıcıyı Aktif Hale Getir
    public void activateUser(Long adminId, Long userId) {
        // Admin doğrulama
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin bulunamadı!"));
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("Bu işlem sadece adminler tarafından yapılabilir.");
        }

        // Kullanıcı doğrulama ve aktif hale getirme
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        if (user.isActive()) {
            throw new IllegalStateException("Bu kullanıcı zaten aktif durumda.");
        }

        user.setSuspendedUntil(null); // Askıya alma kaldırılır
        user.setRole(Role.USER); // Normal kullanıcı yapılıyor
        userRepository.save(user);
    }
}
