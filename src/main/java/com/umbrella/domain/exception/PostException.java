package com.umbrella.dto.exception;

import com.umbrella.exception.BaseException;
import com.umbrella.exception.BaseExceptionType;

public class PostException extends BaseException {
    private BaseExceptionType baseExceptionType;

    public PostException(BaseExceptionType baseExceptionType){
        this.baseExceptionType = baseExceptionType;
    }
    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
