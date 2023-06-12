package com.umbrella.dto.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
