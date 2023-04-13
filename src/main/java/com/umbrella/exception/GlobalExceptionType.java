package com.umbrella.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalExceptionType implements BaseExceptionType{
    ;

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

    
}
