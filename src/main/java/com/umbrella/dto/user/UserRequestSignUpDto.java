package com.umbrella.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.umbrella.constant.Gender;
import com.umbrella.domain.exception.UserException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Calendar;

import static com.umbrella.domain.exception.UserExceptionType.IMPOSSIBLE_AGE_ERROR;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestSignUpDto {

    @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
    private String email;

    private String nickName;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8 ~ 30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @Size(min = 2, max = 100, message = "이름의 길이는 2에서 100 사이여야 합니다.")
    @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "사용자 이름은 2자 이상이면서 한글 혹은 알파벳으로만 이루어져있어야 합니다.")
    private String name;

    @Size(min = 8, max = 8, message = "생년월일은 8자리가 입력되어야 합니다.")
    private String  birthDate;

    private Gender gender;

    @Builder
    public UserRequestSignUpDto(String email, String nickName, String password,
                                String name, String birthDate, String genderValue) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = getGender(genderValue);
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

        if (age < 5) {
            throw new UserException(IMPOSSIBLE_AGE_ERROR);
        }

        return age;
    }

    private Gender getGender(String genderValue) {
        Gender gender = Gender.UNKNOWN;
        return gender.from(genderValue);
    }
}
