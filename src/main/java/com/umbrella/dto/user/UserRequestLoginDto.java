package com.umbrella.dto.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestLoginDto {

    @Email(message = "올바른 형식의 이메일 주소여야 합니다")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8 ~ 20 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @Builder
    public UserRequestLoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
