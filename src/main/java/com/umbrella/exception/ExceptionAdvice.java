package com.umbrella.exception;

import com.umbrella.dto.post.PostExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity handlePostEx(BaseException exception){

        return new ResponseEntity(new PostExceptionDto(exception.getExceptionType().getErrorCode(), exception.getExceptionType().getHttpStatus());
    }
}
