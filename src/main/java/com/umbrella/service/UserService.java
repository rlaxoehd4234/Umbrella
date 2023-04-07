package com.umbrella.project_umbrella.service;

import com.umbrella.project_umbrella.dto.user.UserInfoDto;
import com.umbrella.project_umbrella.dto.user.UserRequestSignUpDto;
import com.umbrella.project_umbrella.dto.user.UserUpdateDto;

public interface UserService {

    void signUp(UserRequestSignUpDto userSignUpDto);

    void update(UserUpdateDto userUpdateDto);

    void updatePassword(String checkPassword, String newPassword);

    void withdraw(String checkPassword);

    UserInfoDto getInfo(Long id);

    UserInfoDto getMyInfo();
}
