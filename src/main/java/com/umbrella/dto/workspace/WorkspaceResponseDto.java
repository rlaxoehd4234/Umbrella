package com.umbrella.dto.workspace;

import com.umbrella.domain.WorkSpace.WorkSpace;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class WorkspaceResponseDto {

    private String title;
    private String description;

    public WorkspaceResponseDto(WorkSpace workSpace){
        this.title = workSpace.getTitle();
        this.description = workSpace.getDescription();
    }
}
