package com.umbrella.dto.chat;

import lombok.Builder;

@Builder
public class ChatMessageLogDto<T> {

    private T chatMessageLog;

}
