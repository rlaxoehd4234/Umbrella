package com.umbrella.dto.board;

import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.service.WorkSpaceService;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardSaveRequestDto {
    private String title;
    @Builder
    public BoardSaveRequestDto( String title){
        this.title = title;
    }
}
