package com.umbrella.controller;

import com.umbrella.dto.board.BoardListResponseDto;
import com.umbrella.dto.board.BoardResponseDto;
import com.umbrella.dto.board.BoardSaveRequestDto;
import com.umbrella.service.Impl.BoardServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@Getter
@RequiredArgsConstructor
public class BoardController {

    private final BoardServiceImpl boardService;

    @PostMapping("/{workspace_id}/create")
    public ResponseEntity<Long> save(@PathVariable Long workspace_id,@Validated @RequestBody BoardSaveRequestDto requestDto){
        return ResponseEntity.ok().body(boardService.save(workspace_id,requestDto));
    }

    @DeleteMapping("/{workspace_id}/delete")
    public ResponseEntity<Long> delete(@PathVariable Long workspace_id){
        return ResponseEntity.ok().body(boardService.delete(workspace_id));
    }

    @GetMapping("/{workspace_id}/{board_id}")
    public ResponseEntity<BoardResponseDto> findById(@PathVariable Long workspace_id, @PathVariable Long board_id){
        return ResponseEntity.ok().body(boardService.findById(workspace_id, board_id));
    }

    @GetMapping("/{workspace_id}/board")
    public ResponseEntity<List<BoardListResponseDto>> findAll(@PathVariable Long workspace_id){
        List<BoardListResponseDto> board = boardService.findAllDesc();
        return ResponseEntity.ok().body(board);
    }



}
