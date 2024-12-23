package com.openhand.openhand.Dto.User.Request;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String location;
}

