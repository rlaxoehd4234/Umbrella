package com.umbrella.service;

import com.umbrella.dto.board.BoardResponseDto;
import com.umbrella.dto.board.BoardSaveRequestDto;
import com.umbrella.dto.board.BoardUpdateRequestDto;
import com.umbrella.dto.workspace.WorkspaceListResponseDto;

import java.util.List;

public interface BoardService {

    Long save(BoardSaveRequestDto requestDto);

    Long update(Long id, BoardUpdateRequestDto requestDto);

    Long delete(Long id);

    BoardResponseDto findById(Long id);

    List<BoardResponseDto> findAllDesc();




}
