package com.umbrella.dto.workspace;

import com.umbrella.domain.WhenToMeet.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventResponseDto {

    private Long eventId;
    private String title;

    public EventResponseDto(Event event) {
        this.eventId = event.getId();
        this.title = event.getTitle();
    }
}
