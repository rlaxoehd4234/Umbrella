package com.umbrella.dto.user;

import lombok.Builder;
import lombok.Getter;

public class UserRequestFindPasswordDto {

    @Getter
    private String email;

    @Getter
    private String password;

    @Builder
    public UserRequestFindPasswordDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
