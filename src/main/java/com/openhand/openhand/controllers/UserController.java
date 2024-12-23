package com.openhand.openhand.controllers;

import com.openhand.openhand.Dto.User.Request.UpdateUserDTO;
import com.openhand.openhand.Dto.User.Request.UpdateUserPasswordDTO;
import com.openhand.openhand.Dto.User.Request.UserRequestDTO;
import com.openhand.openhand.Dto.User.Response.UserResponseDTO;
import com.openhand.openhand.entities.User;
import com.openhand.openhand.exceptions.Result;
import com.openhand.openhand.services.JwtTokenService;
import com.openhand.openhand.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Kullanıcı Kaydı
    @PostMapping("/register")
    public ResponseEntity<Result<UserResponseDTO>> registerUser(@RequestBody UserRequestDTO requestDTO) {
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
        return ResponseEntity.ok(new Result<>(true, "Oturum başarıyla sonlandırıldı.", null));
    }

    //yeni bir Access Token alır
    @PostMapping("/refresh-token")
    public ResponseEntity<Result<String>> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        String newAccessToken = userService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new Result<>(true, "Access token yenilendi.", newAccessToken));
    }

    // Tüm Kullanıcıları Listele
    @GetMapping
    public ResponseEntity<Result> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(new Result<>(true, "Tüm kullanıcılar başarıyla getirildi.", users));
    }

    // Kullanıcı Detaylarını Getir
    @GetMapping("/{id}")
    public ResponseEntity<Result> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla getirildi.", user));
    }

    // Kullanıcı Bilgilerini Güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Result<UserResponseDTO>> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla güncellendi.", updatedUser));
    }

    // Kullanıcı Sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla silindi."));
    }

    //Kullanıcı sifre yenileme
    @PutMapping("/{id}/password")
    public ResponseEntity<Result> updatePassword(@PathVariable Long id, @RequestBody UpdateUserPasswordDTO passwordDTO) {
        userService.updatePassword(id, passwordDTO);
        return ResponseEntity.ok(new Result(true, "Şifre başarıyla güncellendi."));
    }

}
