package com.umbrella.domain.exception;

import com.umbrella.exception.BaseException;
import com.umbrella.exception.BaseExceptionType;
import lombok.Getter;

@Getter
public class WorkspaceException extends BaseException {

    private final BaseExceptionType baseExceptionType;

    public WorkspaceException(BaseExceptionType exceptionType) {
        this.baseExceptionType = exceptionType;
    }
}
