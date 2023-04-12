package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentExceptionType implements BaseExceptionType {
    ;
    private BaseExceptionType exceptionType;

   private int errorCode;
   private HttpStatus httpStatus;
   private String errorMessage;

}
