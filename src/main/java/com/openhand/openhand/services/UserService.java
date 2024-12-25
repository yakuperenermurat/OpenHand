package com.openhand.openhand.services;

import com.openhand.openhand.Dto.User.Request.UpdateUserDTO;
import com.openhand.openhand.Dto.User.Request.UserCreateRequestDTO;
import com.openhand.openhand.Dto.User.Request.UpdateUserPasswordDTO;
import com.openhand.openhand.Dto.User.Response.UserResponseDTO;
import com.openhand.openhand.entities.User;
import com.openhand.openhand.enums.Role;
import com.openhand.openhand.exceptions.ResourceNotFoundException;
import com.openhand.openhand.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenService jwtService;
    // Kullanıcı Kaydı
    public UserResponseDTO registerUser(UserCreateRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Bu e-posta adresi zaten kullanılıyor!");
        }
        if (userRepository.findByPhone(requestDTO.getPhone()) != null) {
            throw new IllegalArgumentException("Bu telefon numarası zaten kullanılıyor!");
        }

        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(requestDTO.getPassword());
        user.setPhone(requestDTO.getPhone());
        user.setLocation(requestDTO.getLocation());
        userRepository.save(user);

        return mapToResponseDTO(user);
    }
    // Kullanıcı Giriş
    public Map<String, String> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı adı veya şifre hatalı!")); // Kullanıcı kontrolü

        // Kullanıcının aktif olup olmadığını kontrol et
        if (!user.isActive()) {
            throw new IllegalStateException("Bu kullanıcı şu anda askıya alınmış durumda.");
        }

        // Şifre doğrulama
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Kullanıcı adı veya şifre hatalı!");
        }

        // Token oluşturma
        String accessToken = jwtService.generateAccessToken(user); // User nesnesi gönderiliyor
        String refreshToken = jwtService.generateRefreshToken(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    public String refreshAccessToken(String refreshToken) {
        // Token doğrulama
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Geçersiz Refresh Token!");
        }

        // Token'dan email çekiliyor
        String email = jwtService.getEmailFromToken(refreshToken);

        // Kullanıcı doğrulama
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        // Yeni Access Token oluşturma
        return jwtService.generateAccessToken(user); // User nesnesi gönderiliyor
    }

    // Tüm Kullanıcıları Listele
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Kullanıcı Detaylarını Getir
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));
    }

    // Kullanıcı Bilgilerini Güncelle
    public UserResponseDTO updateUser(Long id, UpdateUserDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));
        // Email kontrolü: Eğer güncellenen email farklı ise kontrol et
        if (!user.getEmail().equals(updateDTO.getEmail())) {
            if (userRepository.findByEmail(updateDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Bu e-posta adresi zaten kullanılıyor!");
            }
            user.setEmail(updateDTO.getEmail());
        }

        // Phone kontrolü: Eğer güncellenen telefon farklı ise kontrol et
        if (!user.getPhone().equals(updateDTO.getPhone())) {
            if (userRepository.findByPhone(updateDTO.getPhone()) != null) {
                throw new IllegalArgumentException("Bu telefon numarası zaten kullanılıyor!");
            }
            user.setPhone(updateDTO.getPhone());
        }

        user.setName(updateDTO.getName());
        user.setLocation(updateDTO.getLocation());

        userRepository.save(user);

        return mapToResponseDTO(user);
    }

    // Adminin Kullanıcı Sil metodu
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı!");
        }
        userRepository.deleteById(id);
    }

    // Kendi hesabını silme
    public void deleteUserByEmail(String email) {
        // Kullanıcıyı email üzerinden al
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı."));

        // Kullanıcıyı sil
        userRepository.delete(user);
    }


    // Şifre güncelleme için
    public void updatePassword(Long userId, UpdateUserPasswordDTO passwordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı."));

        // Mevcut şifre kontrolü
        if (!user.getPassword().equals(passwordDTO.getOldPassword())) {
            throw new IllegalArgumentException("Mevcut şifre hatalı!");
        }

        // Yeni şifre mevcut şifreyle aynı olmamalı
        if (user.getPassword().equals(passwordDTO.getNewPassword())) {
            throw new IllegalArgumentException("Yeni şifre eski şifreyle aynı olamaz!");
        }

        user.setPassword(passwordDTO.getNewPassword());
        userRepository.save(user);
    }

    // Kullanıcı engelleme
    public void blockUser(Long userId, Long blockedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Engellenecek kullanıcı bulunamadı!"));

        user.getBlockedUsers().add(blockedUser);
        userRepository.save(user);
    }

    // Engellenen kullanıcı kontrolü
    private void checkBlockedUsers(User user, User targetUser) {
        if (user.getBlockedUsers().contains(targetUser)) {
            throw new IllegalArgumentException("Bu kullanıcı engellenmiş!");
        }
    }

    // Kullanıcı engelini kaldırma
    public void unblockUser(Long userId, Long blockedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Engel kaldırılacak kullanıcı bulunamadı!"));

        if (!user.getBlockedUsers().contains(blockedUser)) {
            throw new IllegalArgumentException("Bu kullanıcı engellenmemiş!");
        }

        user.getBlockedUsers().remove(blockedUser);
        userRepository.save(user);
    }


    // Engellenen kullanıcıları  listeleme
    public List<User> getBlockedUsers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));
        return user.getBlockedUsers();
    }


    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setLocation(user.getLocation());
        dto.setRating(user.getRating());
        return dto;
    }
}
