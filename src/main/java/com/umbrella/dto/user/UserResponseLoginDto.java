package com.umbrella.dto.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseLoginDto {

    private Long userId;

    private String nickName;

    private String email;

    @Builder
    public UserResponseLoginDto(Long userId, String nickName, String email) {
        this.userId = userId;
        this.nickName = nickName;
        this.email = email;
    }
}
