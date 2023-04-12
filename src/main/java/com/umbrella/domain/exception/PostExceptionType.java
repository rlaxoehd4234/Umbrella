package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostExceptionType implements BaseExceptionType {

    NOT_FOUND_POST(444,HttpStatus.OK,"존재하지 않는 게시물 입니다.");
    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

}
