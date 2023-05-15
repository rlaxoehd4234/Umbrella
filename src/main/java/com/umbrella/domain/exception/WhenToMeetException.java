package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.Getter;

@Getter
public class WhenToMeetException extends RuntimeException {

    private final BaseExceptionType baseExceptionType;

    public WhenToMeetException(BaseExceptionType exceptionType) {
        this.baseExceptionType = exceptionType;
    }
}
