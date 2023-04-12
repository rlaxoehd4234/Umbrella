package com.umbrella.dto.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum UserExceptionType implements BaseExceptionType {
    UN_AUTHORIZE_ERROR(400, HttpStatus.OK, "권한이 없는 사용자입니다.")
    ;


    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
