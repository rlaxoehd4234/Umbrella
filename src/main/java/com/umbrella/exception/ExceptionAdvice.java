package com.umbrella.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity handlePostEx(BaseException exception){

        return new ResponseEntity(new ExceptionDto(exception.getBaseExceptionType().getErrorCode()), exception.getBaseExceptionType().getHttpStatus());
    }
}
