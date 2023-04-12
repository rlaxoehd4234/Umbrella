package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CommentExceptionType implements BaseExceptionType {
    ;

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;



}
