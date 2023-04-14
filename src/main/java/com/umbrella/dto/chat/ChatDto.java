package com.umbrella.dto.chat;

import lombok.Getter;

@Getter
public class ChatDto {

    // 메시지 타입 : 입장 채팅
    // 메세지 타입에 따라서 동작하는 구조가 달라진다.
    // 입장과 퇴장 -> ENTER, LEAVE 의 경우 입장 / 퇴장 이벤트 처리가 실행,
    // TALK는 말 그대로 해당 채팅방을 sub(구독) 하고 있는 모든 클라이언트에게 전달한다.

   public enum MessageType{
       ENTER, TALK, LEAVE
   }

    private MessageType type;

    private Long roomId; // 방 번호

    private String sender; // 채팅을 보낸 사람

    private String message; // 메세지

    private String time; // 채팅 발송 시간

}
