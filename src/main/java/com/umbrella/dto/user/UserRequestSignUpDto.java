package com.umbrella.project_umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.umbrella.project_umbrella.constant.AuthPlatform;
import com.umbrella.project_umbrella.constant.Gender;
import com.umbrella.project_umbrella.constant.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.Assert;

import javax.validation.constraints.*;
import java.util.Calendar;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestSignUpDto {

    @Email
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickName;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$")
    // 비밀번호는 8 ~ 30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.
    private String password;

    @NotBlank(message = "실명은 필수 입력 값입니다.")
    @Size(min = 2)
    @Pattern(regexp = "^[A-Za-z가-힣]+$")
    // 사용자 이름은 2자 이상이면서 한글 혹은 알파벳으로만 이루어져있어야 합니다.
    private String name;

    @NotBlank(message = "생년월일은 필수 입력 값입니다.")
    private String  birthDate;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private Gender gender;

    public UserRequestSignUpDto(String email, String nickName, String password,
                                String name, String birthDate, Gender gender) {

        Assert.hasText(email, "email must not be blank");
        Assert.hasText(nickName, "nickName must not be blank");
        Assert.hasText(password, "password must not be blank");
        Assert.hasText(name, "mName must not be blank");
        Assert.hasText(birthDate, "birthDate must not be null");
        Assert.notNull(gender, "gender must not be null");

        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserSignUpDtoBuilder {
    }

    public int calculateAge() {
        int birthYear = Integer.parseInt(birthDate.substring(0, 4));
        int birthMonth = Integer.parseInt(birthDate.substring(4, 6));
        int birthDay = Integer.parseInt(birthDate.substring(6));

        return getAge(birthYear, birthMonth, birthDay);
    }

    private int getAge(int birthYear, int birthMonth, int birthDay) {
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1;
        int currentDay = now.get(Calendar.DAY_OF_MONTH);

        int age = (currentYear - birthYear) + 1; // 한국식 나이 계산

        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay) {
            age--;
        }

        return age;
    }
}
