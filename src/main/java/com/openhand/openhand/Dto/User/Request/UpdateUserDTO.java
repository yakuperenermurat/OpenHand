package com.openhand.openhand.Dto.User.Request;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String name;
    private String email;
    private String phone;
    private String location;
}
