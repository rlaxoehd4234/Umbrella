package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WhenToMeetExceptionType implements BaseExceptionType {

    DEFAULT_WHEN2MEET_ERROR(899, HttpStatus.BAD_REQUEST, "알 수 없는 에러가 발생했습니다."),

    /* Event 관련 에러 */
    NOT_FOUND_EVENT_ERROR(800 ,HttpStatus.BAD_REQUEST, "해당 이벤트를 찾을 수 없습니다."),
    ILLEGAL_DATE_RANGE_ERROR(801 ,HttpStatus.BAD_REQUEST, "날짜의 범위가 잘못 지정되었습니다."),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
