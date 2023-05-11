package com.umbrella.service;

import com.umbrella.domain.User.User;
import com.umbrella.dto.user.UserInfoDto;
import com.umbrella.dto.user.UserRequestFindPasswordDto;
import com.umbrella.dto.user.UserRequestSignUpDto;
import com.umbrella.dto.user.UserRequestUpdateDto;
import com.umbrella.dto.workspace.WorkspaceRequestCreateDto;
import com.umbrella.dto.workspace.WorkspaceRequestEnterAndExitDto;

public interface UserService {

    User signUp(UserRequestSignUpDto userSignUpDto);

    void update(UserRequestUpdateDto userUpdateDto);

    void updatePassword(String checkPassword, String newPassword);

    void reCreatePassword(UserRequestFindPasswordDto userRequestFindPasswordDto);

    void withdraw(String checkPassword);

    UserInfoDto getInfo(Long id);

    UserInfoDto getMyInfo();

    Long createWorkspace(WorkspaceRequestCreateDto workspaceCreateDto);

    void enterWorkspace(WorkspaceRequestEnterAndExitDto workspaceRequestEnterDto);

    void exitWorkspace(WorkspaceRequestEnterAndExitDto workspaceRequestEnterDto);
}
