package com.umbrella.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity handlePostEx(BaseException exception){
        return new ResponseEntity(new ExceptionDto(exception.getExceptionType().getErrorCode()),
                                                        exception.getExceptionType().getHttpStatus());
    }

}
