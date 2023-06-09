package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BoardExceptionType implements BaseExceptionType {

    NOT_FOUND_BOARD(444,HttpStatus.NOT_FOUND,"존지하지않는 게시물입니다.")
    ;

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
