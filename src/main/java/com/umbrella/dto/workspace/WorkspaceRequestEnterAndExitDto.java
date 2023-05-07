package com.umbrella.dto.workspace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceRequestEnterAndExitDto {

    @Getter
    private Long id;

    @Getter
    private String title;

    @Builder
    public WorkspaceRequestEnterAndExitDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
