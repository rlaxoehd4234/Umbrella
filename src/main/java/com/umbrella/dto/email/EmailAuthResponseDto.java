package com.umbrella.dto.email;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuthResponseDto {
    @Getter
    private String authKey;

    @Builder
    public EmailAuthResponseDto(String authKey) {
        this.authKey = authKey;
    }
}
