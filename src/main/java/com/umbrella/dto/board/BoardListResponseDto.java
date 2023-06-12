package com.umbrella.dto.board;

import com.umbrella.domain.Board.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardListResponseDto {
    private Long board_id;
    private String title;

    public BoardListResponseDto(Board board){
        this.board_id = board.getId();
        this.title = board.getTitle();
    }
}
