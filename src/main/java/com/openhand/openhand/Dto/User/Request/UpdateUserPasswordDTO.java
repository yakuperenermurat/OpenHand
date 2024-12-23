package com.openhand.openhand.Dto.User.Request;

import lombok.Data;

@Data
public class UpdateUserPasswordDTO {
    private String oldPassword; // Mevcut şifre doğrulaması için
    private String newPassword;
}

