package com.openhand.openhand.controllers;

import com.openhand.openhand.Dto.User.Request.UpdateUserDTO;
import com.openhand.openhand.Dto.User.Request.UpdateUserPasswordDTO;
import com.openhand.openhand.Dto.User.Request.UserCreateRequestDTO;
import com.openhand.openhand.Dto.User.Response.UserResponseDTO;
import com.openhand.openhand.entities.User;
import com.openhand.openhand.exceptions.Result;
import com.openhand.openhand.services.AdminService;
import com.openhand.openhand.services.JwtTokenService;
import com.openhand.openhand.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenService jwtService;
    @Autowired
    private AdminService adminService;

    // Kullanıcı Kaydı
    @PostMapping("/register")
    public ResponseEntity<Result<UserResponseDTO>> registerUser(@Valid @RequestBody UserCreateRequestDTO requestDTO) {
        UserResponseDTO userResponse = userService.registerUser(requestDTO);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla kaydedildi.", userResponse));
    }


    // Kullanıcı Giriş
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, String>>> loginUser(@RequestParam String email, @RequestParam String password) {
        Map<String, String> tokens = userService.login(email, password);
        return ResponseEntity.ok(new Result<>(true, "Giriş başarılı. Tokenlar oluşturuldu.", tokens));
    }
    // Kullanıcı Logout
    @PostMapping("/logout")
    public ResponseEntity<Result<String>> logoutUser(@RequestHeader("Authorization") String refreshToken) {
        jwtService.invalidateToken(refreshToken);
        return ResponseEntity.ok(new Result<>(true, "Oturum başarıyla sonlandırıldı."));
    }

    //yeni bir Access Token alır
    @PostMapping("/refresh-token")
    public ResponseEntity<Result<String>> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        String newAccessToken = userService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new Result<>(true, "Access token yenilendi.", newAccessToken));
    }

    // Kullanıcı Detaylarını Getir
    @GetMapping("/{id}")
    public ResponseEntity<Result> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla getirildi.", user));
    }

    // Kullanıcı Bilgilerini Güncelle
    @PutMapping("/me")
    public ResponseEntity<Result<UserResponseDTO>> updateUser(@Valid @RequestBody UpdateUserDTO updateDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = adminService.findUserByEmail(email); // Kullanıcı email ile bulunur
        UserResponseDTO updatedUser = userService.updateUser(user.getUserId(), updateDTO);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla güncellendi.", updatedUser));
    }

    // Kullanıcı Sil
    @DeleteMapping("/me")
    public ResponseEntity<Result<String>> deleteMyAccount() {
        // Kullanıcıyı SecurityContext'ten al
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUserByEmail(email); // Kullanıcının kendi hesabını sil
        return ResponseEntity.ok(new Result<>(true, "Hesabınız başarıyla silindi."));
    }

    // Kullanıcı şifre yenileme
    @PutMapping("/{id}/password")
    public ResponseEntity<Result> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdateUserPasswordDTO passwordDTO) {
        userService.updatePassword(id, passwordDTO);
        return ResponseEntity.ok(new Result(true, "Şifre başarıyla güncellendi."));
    }

    //Kullanıcı Engelleme
    @PutMapping("/{userId}/block/{blockedUserId}")
    public ResponseEntity<Result<String>> blockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        userService.blockUser(userId, blockedUserId);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla engellendi."));
    }

    // Kullanıcı Engelini Kaldırma
    @PutMapping("/{userId}/unblock/{blockedUserId}")
    public ResponseEntity<Result<String>> unblockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        userService.unblockUser(userId, blockedUserId);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı engeli başarıyla kaldırıldı."));
    }

    // Kullanıcının Engellediklerini Listeleme
    @GetMapping("/{userId}/blocked-users")
    public ResponseEntity<Result<List<User>>> getBlockedUsers(@PathVariable Long userId) {
        List<User> blockedUsers = userService.getBlockedUsers(userId);
        return ResponseEntity.ok(new Result<>(true, "Engellenen kullanıcılar başarıyla listelendi.", blockedUsers));
    }

}
