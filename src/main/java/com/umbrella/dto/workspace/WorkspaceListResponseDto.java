package com.umbrella.dto.workspace;

import com.umbrella.domain.WorkSpace.WorkSpace;
import lombok.Getter;

@Getter
public class WorkspaceListResponseDto {

    private String title;

    public WorkspaceListResponseDto(WorkSpace workSpace){
        this.title = workSpace.getTitle();
    }
}
