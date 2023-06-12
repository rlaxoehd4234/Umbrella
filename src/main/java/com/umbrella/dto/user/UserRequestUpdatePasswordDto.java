package com.umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestUpdatePasswordDto {

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8 ~ 20 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.")
    private String checkPassword;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8 ~ 20 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.")
    private String newPassword;

    @Builder
    public UserRequestUpdatePasswordDto(String checkPassword, String newPassword) {
        this.checkPassword = checkPassword;
        this.newPassword = newPassword;
    }
}
