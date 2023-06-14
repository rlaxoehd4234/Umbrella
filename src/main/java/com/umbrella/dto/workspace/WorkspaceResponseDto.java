package com.umbrella.dto.workspace;

import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.dto.board.BoardResponseDto;
import com.umbrella.dto.user.UserInWorkspaceDTO;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Data
public class WorkspaceResponseDto {

    private String title;
    private String description;
    private List<UserInWorkspaceDTO> userListInWorkspace;
    private List<BoardResponseDto> boards;
    private List<EventResponseDto> events;

    public WorkspaceResponseDto(WorkSpace workSpace) {
        this.title = workSpace.getTitle();
        this.description = workSpace.getDescription();
        this.userListInWorkspace = workSpace.getWorkspaceUsers().stream().map(user -> new UserInWorkspaceDTO(user.getWorkspaceUser())).collect(Collectors.toList());
        this.boards = workSpace.getBoards().stream().map(BoardResponseDto::new).collect(Collectors.toList());
        this.events = workSpace.getEvents().stream().map(EventResponseDto::new).collect(Collectors.toList());
    }
}
