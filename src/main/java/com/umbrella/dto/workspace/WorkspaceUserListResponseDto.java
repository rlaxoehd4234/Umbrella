package com.umbrella.dto.workspace;

import com.umbrella.domain.WorkSpace.WorkspaceUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WorkspaceUserListResponseDto {
    private String title;

    public WorkspaceUserListResponseDto(WorkspaceUser workspaceUser){
        this.title = workspaceUser.getWorkspace().getTitle();
    }
}
