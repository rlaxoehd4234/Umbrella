package com.umbrella.dto.chat;

import lombok.Builder;

import java.util.List;


@Builder
public class ChatRoomsResponseDto<T>{

    private T chatRoomNameList;
}
