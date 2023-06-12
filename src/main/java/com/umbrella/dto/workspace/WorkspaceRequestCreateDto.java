package com.umbrella.dto.workspace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceRequestCreateDto {

    @Getter
    private String title;

    @Getter
    private String description;

    @Builder
    public WorkspaceRequestCreateDto(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
