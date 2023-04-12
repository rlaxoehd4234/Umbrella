package com.umbrella.domain.exception;


import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserExceptionType implements BaseExceptionType {
    UN_AUTHORIZE_ERROR(400, HttpStatus.OK, "권한이 없는 사용자입니다."),
    NOT_FOUND_ERROR(400,HttpStatus.OK, "존재하지 않는 사용자입니다.")
    ;


    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

}
