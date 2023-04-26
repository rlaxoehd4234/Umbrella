package com.umbrella.dto.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawUserDto {

    @Getter
    private String password;

    @Builder
    public WithdrawUserDto(String password) {
        this.password = password;
    }
}
