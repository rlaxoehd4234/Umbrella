package com.umbrella.dto.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawUserDto {

    private String password;

    @Builder
    public WithdrawUserDto(String password) {
        this.password = password;
    }
}
