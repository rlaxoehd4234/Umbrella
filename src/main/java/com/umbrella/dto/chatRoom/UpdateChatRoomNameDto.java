package com.umbrella.dto.chatRoom;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateChatRoomNameDto {

    private Long chatRoomId;

    private String updateChatRoomName;

    @Builder
    public UpdateChatRoomNameDto(String updateChatRoomName, Long chatRoomId) {
        this.updateChatRoomName = updateChatRoomName;
        this.chatRoomId = chatRoomId;
    }
}
