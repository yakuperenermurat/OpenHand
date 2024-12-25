package com.openhand.openhand.Dto.User.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequestDTO {

    @NotBlank(message = "İsim boş olamaz.")
    private String name;

    @NotBlank(message = "E-posta boş olamaz.")
    @Email(message = "Geçersiz e-posta formatı.")
    private String email;

    @NotBlank(message = "Şifre boş olamaz.")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır.")
    private String password;

    @NotBlank(message = "Telefon numarası boş olamaz.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Telefon numarası 10 haneli olmalıdır ve sadece rakamlardan oluşmalıdır.")
    private String phone;

    @NotBlank(message = "Konum boş olamaz.")
    private String location;
}
