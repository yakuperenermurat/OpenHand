package com.openhand.openhand.Dto.User.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserPasswordDTO {

    @NotBlank(message = "Mevcut şifre boş bırakılamaz.")
    private String oldPassword; // Mevcut şifre doğrulaması için

    @NotBlank(message = "Yeni şifre boş bırakılamaz.")
    @Size(min = 8, message = "Yeni şifre en az 8 karakter olmalıdır.")
    private String newPassword;
}
