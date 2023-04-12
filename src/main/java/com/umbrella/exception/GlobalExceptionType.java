package com.umbrella.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalExceptionType implements BaseExceptionType{

    ;

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

}
