package com.umbrella.domain.exception;

import com.umbrella.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ChatRoomExceptionType implements BaseExceptionType {

    DUPLICATED_CHATROOM_NAME(444,HttpStatus.OK,"중복된 채팅방 이름입니다."),
    NOT_FOUND_CHATROOM(444,HttpStatus.OK,"중복된 채팅방 이름입니다."),
    NOT_OWNER(444,HttpStatus.OK,"채팅방을 만든 유저가 아닙니다.")
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
