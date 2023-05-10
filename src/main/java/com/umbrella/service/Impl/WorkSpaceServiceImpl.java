package com.umbrella.service.Impl;

import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import com.umbrella.domain.WorkSpace.WorkspaceUserRepository;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.UserExceptionType;
import com.umbrella.domain.exception.WorkspaceException;
import com.umbrella.domain.exception.WorkspaceExceptionType;
import com.umbrella.dto.workspace.*;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {
    private final WorkSpaceRepository workSpaceRepository;
    private final SecurityUtil securityUtil;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;

    public Long save(WorkspaceRequestDto requestDto){

        WorkSpace workSpace = WorkSpace.builder().
                title(requestDto.getTitle()).
                description(requestDto.getDescription())
                .build();

        return workSpaceRepository.save(workSpace).getId();
    }

    @Override
    public Long update(Long id, WorkspaceUpdateRequestDto requestDto) {
        WorkSpace workSpace = validateWorkspace(id);
        workSpace.update(requestDto.getTitle(), requestDto.getDescription());

        return id;
    }

    @Override
    public Long delete(Long id) {
        WorkSpace workSpace = validateWorkspace(id);
        workSpaceRepository.delete(workSpace);
        return id;
    }

    @Override
    public WorkspaceResponseDto findById(Long id) {
        WorkSpace workSpace = validateWorkspace(id);
        return new WorkspaceResponseDto(workSpace) ;
    }

    public List<WorkspaceUserListResponseDto> findAllList(){
        User user = userRepository.findById(securityUtil.getLoginUserId()).
                orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        return user.getWorkspaceUsers().stream()
                .map(WorkspaceUserListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkspaceListResponseDto> findAllDesc() {
        return workSpaceRepository.findAll().stream().map(WorkspaceListResponseDto::new).collect(Collectors.toList());
    }

    public WorkSpace validateWorkspace(Long id){

        return workSpaceRepository.findById(id).orElseThrow(() -> new WorkspaceException(WorkspaceExceptionType.NOT_FOUNT_WORKSPACE));
    }
}
