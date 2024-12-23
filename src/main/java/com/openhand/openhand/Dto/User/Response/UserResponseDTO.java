package com.openhand.openhand.Dto.User.Response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String location;
    private Double rating; // Feedbacklerden gelen puan ortalaması
}