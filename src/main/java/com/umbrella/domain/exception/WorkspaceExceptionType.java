package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WorkspaceExceptionType implements BaseExceptionType {
    DEFAULT_WORKSPACE_ERROR(700, HttpStatus.BAD_REQUEST, "잘못된 워크스페이스 관련 형식입니다."),
    ALREADY_ENTERED_WORKSPACE_ERROR(701 ,HttpStatus.BAD_REQUEST, "이미 입장한 워크스페이스 입니다."),
    BLANK_WORKSPACE_TITLE_ERROR(702 ,HttpStatus.BAD_REQUEST, "워크스페이스의 제목은 필수 입력 값입니다."),
    BLANK_WORKSPACE_DESCRIPTION_ERROR(703 ,HttpStatus.BAD_REQUEST, "워크스페이스의 설명은 필수 입력 값입니다."),
    NOT_FOUND_WORKSPACE(400, HttpStatus.BAD_REQUEST, "존재하지 않는 워크스페이스입니다.")
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
