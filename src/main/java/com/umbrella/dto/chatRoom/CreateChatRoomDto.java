package com.umbrella.dto.chatRoom;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Data
public class CreateChatRoomDto {

    // 작성자 (nickName)
    @NotNull
    @NotBlank
    @Size(min = 2, max = 500, message = "1자 이상, 500자 이하만 가능합니다.")
    private String roomName;

    @NotNull
    private Long workSpaceId;

}
