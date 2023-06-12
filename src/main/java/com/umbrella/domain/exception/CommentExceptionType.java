package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CommentExceptionType implements BaseExceptionType {

    NOT_FOUND_COMMENT(444,HttpStatus.OK,"존재하지 않는 댓글입니다."),
    NOT_FOUND_CHILD_COMMENT(444, HttpStatus.OK, "존재하지 않는 대댓글입니다."),
    ALREADY_DELETED_COMMENT(444, HttpStatus.OK, "이미 삭제된 댓글입니다.")
    ;


    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;


}
