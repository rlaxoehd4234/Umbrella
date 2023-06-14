package com.umbrella.service.Impl;

import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
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
    private final UserRepository userRepository;
    private final WorkspaceUserRepository workspaceUserRepository;

    // TODO: 삭제 권한 셍성

    @Override
    public Long update(Long id, WorkspaceUpdateRequestDto requestDto) {
        WorkSpace workSpace = validateWorkspace(id);
        validateWorkSpaceValidUser();
        workSpace.update(requestDto.getTitle(), requestDto.getDescription());

        return id;
    }


//    @Override
//    public Long delete(Long id) {
//        WorkSpace workSpace = validateWorkspace(id);
//        workSpaceRepository.delete(workSpace);
//        return id;
//    }

    @Override
    public WorkspaceResponseDto findById(Long id) {
        validateWorkSpaceValidUser();
        WorkSpace workSpace = validateWorkspace(id);
        return new WorkspaceResponseDto(workSpace) ;
    }

    public List<WorkspaceUserListResponseDto> findAllList(){
        validateWorkSpaceValidUser();
        User user = userRepository.findById(securityUtil.getLoginUserId()).
                orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        return user.getWorkspaceUsers().stream()
                .map(WorkspaceUserListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkspaceListResponseDto> findAllDesc() {
        validateWorkSpaceValidUser();
        return workSpaceRepository.findAll().stream().map(WorkspaceListResponseDto::new).collect(Collectors.toList());
    }

    public WorkSpace validateWorkspace(Long id){

        return workSpaceRepository.findById(id).orElseThrow(() -> new WorkspaceException(WorkspaceExceptionType.NOT_FOUND_WORKSPACE));
    }

    public void validateWorkSpaceValidUser()  {
        User user = userRepository.findById(securityUtil.getLoginUserId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_ERROR));

        // TODO: 이거 이 상태로 두면 유저 처음 가입했을 때, 소속된 Workspace 가 존재하지 않아서 권한 없음 에러뜨니까 수정 부탁드립니다. -승빈
//        if(workspaceUserRepository.findByWorkspaceUser(user).isEmpty()){
//            throw new UserException(UserExceptionType.UN_AUTHORIZE_ERROR);
//        }
    }
}
