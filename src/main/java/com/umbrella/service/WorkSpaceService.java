package com.umbrella.service;

import com.umbrella.dto.workspace.WorkspaceListResponseDto;
import com.umbrella.dto.workspace.WorkspaceRequestDto;
import com.umbrella.dto.workspace.WorkspaceResponseDto;
import com.umbrella.dto.workspace.WorkspaceUpdateRequestDto;

import java.util.List;

public interface WorkSpaceService {

    Long save(WorkspaceRequestDto requestDto);

    Long update(Long id, WorkspaceUpdateRequestDto requestDto);

    Long delete(Long id);

    WorkspaceResponseDto findById(Long id);

    List<WorkspaceListResponseDto> findAllDesc();
}
