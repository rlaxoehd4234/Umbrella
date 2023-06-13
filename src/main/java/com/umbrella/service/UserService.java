package com.umbrella.service;

import com.umbrella.domain.User.User;
import com.umbrella.dto.user.*;
import com.umbrella.dto.workspace.WorkspaceRequestCreateDto;
import com.umbrella.dto.workspace.WorkspaceRequestEnterAndExitDto;
import com.umbrella.dto.workspace.WorkspaceResponseDto;

import javax.servlet.http.HttpServletResponse;

public interface UserService {

    User signUp(UserRequestSignUpDto userSignUpDto);

    UserResponseLoginDto login(UserRequestLoginDto userLoginDto, HttpServletResponse response);

    UserResponseUpdateDto update(UserRequestUpdateDto userUpdateDto);

    void updatePassword(String checkPassword, String newPassword);

    void reCreatePassword(UserRequestFindPasswordDto userRequestFindPasswordDto);

    void withdraw(String checkPassword);

    UserInfoDto getInfo(Long id);

    UserInfoDto getMyInfo();

    Long createWorkspace(WorkspaceRequestCreateDto workspaceCreateDto);

    WorkspaceResponseDto enterWorkspace(WorkspaceRequestEnterAndExitDto workspaceRequestEnterDto);

    void exitWorkspace(WorkspaceRequestEnterAndExitDto workspaceRequestEnterDto);
}
