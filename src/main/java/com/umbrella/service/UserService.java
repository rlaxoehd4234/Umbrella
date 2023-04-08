package com.umbrella.service;

import com.umbrella.dto.user.UserInfoDto;
import com.umbrella.dto.user.UserRequestSignUpDto;
import com.umbrella.dto.user.UserUpdateDto;

public interface UserService {

    void signUp(UserRequestSignUpDto userSignUpDto);

    void update(UserUpdateDto userUpdateDto);

    void updatePassword(String checkPassword, String newPassword);

    void withdraw(String checkPassword);

    UserInfoDto getInfo(Long id);

    UserInfoDto getMyInfo();
}
