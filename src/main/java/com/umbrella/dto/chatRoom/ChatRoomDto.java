package com.umbrella.dto.chatRoom;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@NoArgsConstructor
@Data
public class ChatRoomDto {

    private Long chatRoomId;

    @NotNull
    @NotBlank
    private String roomName;

    @NotNull
    private String createdBy;


    @Builder
    public ChatRoomDto(Long chatRoomId,String roomName, String createdBy) {
        this.chatRoomId = chatRoomId;
        this.roomName = roomName;
        this.createdBy = createdBy;
    }
}
