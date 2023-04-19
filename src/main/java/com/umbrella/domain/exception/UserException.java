package com.umbrella.domain.exception;


import com.umbrella.exception.BaseException;
import com.umbrella.exception.BaseExceptionType;
import lombok.Getter;

@Getter
public class UserException extends BaseException {

    private BaseExceptionType baseExceptionType;

    public UserException(BaseExceptionType exceptionType) {
        this.baseExceptionType = exceptionType;
    }
}
