package com.openhand.openhand.Dto.User.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @NotBlank(message = "İsim boş bırakılamaz.")
    @Size(max = 50, message = "İsim en fazla 50 karakter olabilir.")
    private String name;

    @NotBlank(message = "E-posta boş bırakılamaz.")
    @Email(message = "Geçersiz e-posta formatı.")
    private String email;

    @NotBlank(message = "Telefon numarası boş bırakılamaz.")
    @Pattern(regexp = "^\\d{10}$", message = "Telefon numarası 10 haneli olmalıdır ve sadece rakamlardan oluşmalıdır.")
    private String phone;

    @NotBlank(message = "Konum bilgisi boş bırakılamaz.")
    @Size(max = 100, message = "Konum bilgisi en fazla 100 karakter olabilir.")
    private String location;
}
