package com.umbrella.dto.exception;

import com.umbrella.exception.BaseException;
import com.umbrella.exception.BaseExceptionType;

public class UserException extends BaseException {

    private BaseExceptionType baseExceptionType;

    public UserException(BaseExceptionType baseExceptionType){
        this.baseExceptionType = baseExceptionType;
    }
    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
