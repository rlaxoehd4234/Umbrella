package com.umbrella.dto.board;

import com.umbrella.domain.Board.Board;
import lombok.Getter;

@Getter
public class BoardResponseDto {

    private String title;


    public BoardResponseDto(Board board){
        this.title = board.getTitle();
    }
}
