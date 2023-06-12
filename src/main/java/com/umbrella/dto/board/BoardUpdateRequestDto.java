package com.umbrella.dto.board;

import com.umbrella.service.BoardService;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardUpdateRequestDto {

    private String title;

    @Builder
    public BoardUpdateRequestDto(String title){
        this.title = title;
    }
}
