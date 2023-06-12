package com.umbrella.dto.board;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.umbrella.domain.Board.Board;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BoardResponseDto {

    private Long board_id;
    private String title;

    public BoardResponseDto(Board board){
        this.board_id = board.getId();
        this.title = board.getTitle();
    }
}
