package com.umbrella.domain.exception;


import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserExceptionType implements BaseExceptionType {

    /* SignUp DTO & Login DTO & Update Password DTO & Withdraw DTO Exceptions */
    DEFAULT_ERROR(699 ,HttpStatus.BAD_REQUEST, "잘못된 인자가 삽입되었습니다."),
    BLANK_PASSWORD_ERROR(600, HttpStatus.BAD_REQUEST, "비밀번호는 필수 입력 값입니다."),
    BLANK_EMAIL_ERROR(601, HttpStatus.BAD_REQUEST, "이메일은 필수 입력 값입니다."),
    BLANK_NICKNAME_ERROR(602, HttpStatus.BAD_REQUEST, "닉네임은 필수 입력 값입니다."),
    BLANK_NAME_ERROR(603, HttpStatus.BAD_REQUEST, "이름은 필수 입력 값입니다."),
    BLANK_BIRTHDATE_ERROR(604, HttpStatus.BAD_REQUEST, "생년월일은 필수 입력 값입니다."),
    BLANK_GENDER_ERROR(605, HttpStatus.BAD_REQUEST, "성별은 필수 입력 값입니다."),
    UNSUPPORTED_GENDER_ERROR(606, HttpStatus.BAD_REQUEST, "지원하지 않는 성별입니다."),


    /* UserService Exceptions & Custom OAuth2 User Service */
    DUPLICATE_EMAIL_ERROR(607, HttpStatus.BAD_REQUEST, "동일한 이메일을 사용하는 계정이 이미 존재합니다."),
    DUPLICATE_NICKNAME_ERROR(608, HttpStatus.BAD_REQUEST, "동일한 닉네임을 사용하는 계정이 이미 존재합니다."),
    INCONSISTENCY_PASSWORD_ERROR(609, HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    ENTITY_NOT_FOUND_ERROR(610, HttpStatus.BAD_REQUEST, "해당 정보를 가진 계정이 존재하지 않습니다."),
    IMPOSSIBLE_AGE_ERROR(611, HttpStatus.BAD_REQUEST, "5세 미만의 영유아는 본 서비스를 이용할 수 없습니다."),

    /* Json Processing Filter */
    UNSUPPORTED_HTTP_METHOD(612, HttpStatus.UNAUTHORIZED, "올바르지 않은 요청 형식입니다."),

    /* OAuth2 User Info Factory */
    UNSUPPORTED_PLATFORM(620, HttpStatus.BAD_REQUEST, "소셜 로그인을 지원하지 않는 플랫폼입니다."),

    UN_AUTHORIZE_ERROR(400, HttpStatus.OK, "권한이 없는 사용자입니다."),
    NOT_FOUND_ERROR(400, HttpStatus.OK, "존재하지 않는 사용자입니다.")
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
