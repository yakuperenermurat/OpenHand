package com.openhand.openhand.services;

import com.openhand.openhand.Dto.User.Request.UpdateUserDTO;
import com.openhand.openhand.Dto.User.Request.UserRequestDTO;
import com.openhand.openhand.Dto.User.Request.UpdateUserPasswordDTO;
import com.openhand.openhand.Dto.User.Response.UserResponseDTO;
import com.openhand.openhand.entities.User;
import com.openhand.openhand.exceptions.ResourceNotFoundException;
import com.openhand.openhand.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenService jwtService;
    // Kullanıcı Kaydı
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()) != null) {
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
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            String accessToken = jwtService.generateAccessToken(user.getEmail());
            String refreshToken = jwtService.generateRefreshToken(user.getEmail());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;
        }
        throw new IllegalArgumentException("Kullanıcı adı veya şifre hatalı!");
    }
    public String refreshAccessToken(String refreshToken) {
        if (jwtService.validateRefreshToken(refreshToken)) {
            String email = jwtService.getEmailFromToken(refreshToken);
            return jwtService.generateAccessToken(email);
        }
        throw new IllegalArgumentException("Geçersiz Refresh Token!");
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
            if (userRepository.findByEmail(updateDTO.getEmail()) != null) {
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

    // Kullanıcı Sil
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı!");
        }
        userRepository.deleteById(id);
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
