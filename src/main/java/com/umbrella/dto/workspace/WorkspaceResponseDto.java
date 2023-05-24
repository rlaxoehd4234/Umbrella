package com.umbrella.dto.workspace;

import com.umbrella.domain.WhenToMeet.Event;
import com.umbrella.domain.WorkSpace.WorkSpace;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Data
public class WorkspaceResponseDto {

    private String title;
    private String description;
    private List<EventResponseDto> events;

    public WorkspaceResponseDto(WorkSpace workSpace){
        this.title = workSpace.getTitle();
        this.description = workSpace.getDescription();
        this.events = workSpace.getEvents().stream().map(EventResponseDto::new).collect(Collectors.toList());
    }
}
