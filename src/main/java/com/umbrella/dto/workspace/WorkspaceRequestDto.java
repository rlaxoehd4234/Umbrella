package com.umbrella.dto.workspace;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WorkspaceRequestDto {

    private String title;

    private String description;

    @Builder
    public WorkspaceRequestDto(String title, String description){
        this.title = title;
        this.description =description;
    }


}
