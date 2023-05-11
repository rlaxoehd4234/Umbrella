package com.umbrella.dto.email;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailPostRequestDto {

    @Getter
    @Email(message = "올바른 형식의 이메일 주소여야 합니다")
    private String email;

    @Builder
    public EmailPostRequestDto(String email) {
        this.email = email;
    }
}
