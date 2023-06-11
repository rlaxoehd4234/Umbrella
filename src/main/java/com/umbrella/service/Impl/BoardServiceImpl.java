package com.umbrella.service.Impl;

import com.umbrella.domain.Board.Board;
import com.umbrella.domain.Board.BoardRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import com.umbrella.domain.WorkSpace.WorkspaceUserRepository;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.UserExceptionType;
import com.umbrella.domain.exception.WorkspaceException;
import com.umbrella.domain.exception.WorkspaceExceptionType;
import com.umbrella.dto.board.BoardListResponseDto;
import com.umbrella.dto.board.BoardResponseDto;
import com.umbrella.dto.board.BoardSaveRequestDto;
import com.umbrella.dto.board.BoardUpdateRequestDto;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final SecurityUtil securityUtil;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;

    @Override
    public Long save(Long workspace_id, BoardSaveRequestDto requestDto) {
        validateUser();
        Board board = validateWorkSpace(requestDto, workspace_id);
        return boardRepository.save(board).getId();
    }

    @Override
    public Long update(Long id , BoardUpdateRequestDto requestDto) {
        validateUser();
        Board board = validateBoard(id);
        board.update(requestDto.getTitle());
        return id;
    }

    @Override
    public Long delete(Long id) {
        validateUser();
        Board board = validateBoard(id);
        boardRepository.delete(board);
        return id;
    }

    @Override
    public BoardResponseDto findById(Long workspace_id, Long board_id) {
        validateUser();
        Board board = validateBoard(board_id);
        return new BoardResponseDto(board);
    }

    @Override
    public List<BoardListResponseDto> findAllDesc() {
        validateUser();
        return boardRepository.findAll().stream().map(BoardListResponseDto::new).collect(Collectors.toList());
    }


    private Board validateWorkSpace(BoardSaveRequestDto requestDto, Long id){
        if(workSpaceRepository.findById(id).isEmpty()){
            throw new WorkspaceException(WorkspaceExceptionType.NOT_FOUND_WORKSPACE);
        }
        return Board.builder()
                .title(requestDto.getTitle())
                .workSpace(workSpaceRepository.findById(id).orElseThrow(() -> new WorkspaceException(WorkspaceExceptionType.NOT_FOUND_WORKSPACE)))
                .build();
    }
    private Board validateBoard(Long id){

        return boardRepository.findById(id).orElseThrow(() -> new WorkspaceException(WorkspaceExceptionType.NOT_FOUND_WORKSPACE));
    }

    private void validateUser(){
        User user = userRepository.findById(securityUtil.getLoginUserId()).orElseThrow(()-> new UserException(UserExceptionType.NOT_FOUND_ERROR));
        if(workspaceUserRepository.findByWorkspaceUser(user).isEmpty()){
            throw new UserException(UserExceptionType.UN_AUTHORIZE_ERROR);
        }
    }
}
