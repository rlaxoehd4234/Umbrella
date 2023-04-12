package com.umbrella.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GlobalException extends BaseException{

    private BaseExceptionType exceptionType;


}
