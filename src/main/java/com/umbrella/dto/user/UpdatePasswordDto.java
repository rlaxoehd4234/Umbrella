package com.umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdatePasswordDto {

    private final String checkPassword;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8 ~ 30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.")
    private final String newPassword;

    @Builder
    public UpdatePasswordDto(String checkPassword, String newPassword) {
        this.checkPassword = checkPassword;
        this.newPassword = newPassword;
    }
}
