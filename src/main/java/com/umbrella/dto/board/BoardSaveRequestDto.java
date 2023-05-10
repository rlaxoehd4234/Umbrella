package com.umbrella.dto.board;

import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.service.WorkSpaceService;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardSaveRequestDto {

    private Long workSpace_id; //workSpace id;
    private String title;
    @Builder
    public BoardSaveRequestDto(Long workSpace_id, String title){
        this.workSpace_id = workSpace_id;
        this.title = title;
    }
}
