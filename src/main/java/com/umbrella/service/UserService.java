package com.umbrella.service;

import com.umbrella.domain.User.User;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import com.umbrella.dto.user.UserInfoDto;
import com.umbrella.dto.user.UserRequestSignUpDto;
import com.umbrella.dto.user.UserUpdateDto;
import com.umbrella.dto.workspace.WorkspaceRequestCreateDto;

public interface UserService {

    User signUp(UserRequestSignUpDto userSignUpDto);

    void update(UserUpdateDto userUpdateDto);

    void updatePassword(String checkPassword, String newPassword);

    void withdraw(String checkPassword);

    UserInfoDto getInfo(Long id);

    UserInfoDto getMyInfo();

    WorkspaceUser createWorkspace(WorkspaceRequestCreateDto workspaceCreateDto);

    void exitWorkspace(Long workspaceId);
}
