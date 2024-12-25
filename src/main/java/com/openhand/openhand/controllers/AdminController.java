package com.openhand.openhand.controllers;

import com.openhand.openhand.Dto.User.Request.UpdateUserDTO;
import com.openhand.openhand.Dto.User.Response.UserResponseDTO;
import com.openhand.openhand.entities.User;
import com.openhand.openhand.exceptions.Result;
import com.openhand.openhand.services.AdminService;
import com.openhand.openhand.services.JwtTokenService;
import com.openhand.openhand.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtTokenService jwtService;

    @Autowired
    private UserService userService;

    // Kullanıcıyı Askıya Alma
    @PutMapping("/suspend/{userId}")
    public ResponseEntity<Result<String>> suspendUser(
            @RequestHeader("Authorization") String adminToken,
            @PathVariable Long userId,
            @RequestParam(required = false) String until // Opsiyonel: Süresiz için boş bırakılabilir
    ) {
        String adminEmail = jwtService.getEmailFromToken(adminToken);
        User admin = adminService.findUserByEmail(adminEmail); // Admin doğrulama
        LocalDateTime suspendUntil = until != null ? LocalDateTime.parse(until) : null;
        adminService.suspendUser(admin.getUserId(), userId, suspendUntil);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla askıya alındı."));
    }

    // Tüm Kullanıcıları Listele
    @GetMapping("/users")
    public ResponseEntity<Result<List<User>>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(new Result<>(true, "Tüm kullanıcılar başarıyla listelendi.", users));
    }

    // Kullanıcıyı Aktif Hale Getirme
    @PutMapping("/activate/{userId}")
    public ResponseEntity<Result<String>> activateUser(@RequestHeader("Authorization") String adminToken, @PathVariable Long userId) {
        String adminEmail = jwtService.getEmailFromToken(adminToken);
        User admin = adminService.findUserByEmail(adminEmail); // Admin doğrulama
        adminService.activateUser(admin.getUserId(), userId);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla aktif hale getirildi."));
    }

    // Kullanıcıyı güncelleme
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<UserResponseDTO>> adminUpdateUser(
            @PathVariable Long id, @Valid @RequestBody UpdateUserDTO updateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla güncellendi.", updatedUser));
    }

    // Kullanıcı silme
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<String>> adminDeleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new Result<>(true, "Kullanıcı başarıyla silindi."));
    }
}


