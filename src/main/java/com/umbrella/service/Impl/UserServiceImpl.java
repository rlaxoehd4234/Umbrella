package com.umbrella.service.Impl;

import com.umbrella.domain.User.User;
import com.umbrella.domain.exception.UserException;
import com.umbrella.dto.user.UserInfoDto;
import com.umbrella.dto.user.UserRequestSignUpDto;
import com.umbrella.dto.user.UserUpdateDto;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.umbrella.domain.exception.UserExceptionType.*;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityUtil securityUtil;

    private User userSignUpDtoToEntity(UserRequestSignUpDto userRequestSignUpDto) {

        User user = User.builder()
                .email(userRequestSignUpDto.getEmail())
                .nickName(userRequestSignUpDto.getNickName())
                .password(userRequestSignUpDto.getPassword())
                .name(userRequestSignUpDto.getName())
                .age(userRequestSignUpDto.calculateAge())
                .gender(userRequestSignUpDto.getGender())
                .build();

        return user;
    }

    private User getUserByEmail() {
        return userRepository.findByEmail(securityUtil.getLoginUserEmail()).orElseThrow(
                () -> new UserException(ENTITY_NOT_FOUND_ERROR)
        );
    }

    @Override
    public void signUp(UserRequestSignUpDto userSignUpDto) {
        User signUpUser = userSignUpDtoToEntity(userSignUpDto);

        signUpUser.addUserAuthorities();
        signUpUser.addDefaultPlatform();
        signUpUser.encodePassword(passwordEncoder);

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new UserException(DUPLICATE_EMAIL_ERROR);
        } else if (userRepository.findByNickName(userSignUpDto.getNickName()).isPresent()) {
            throw new UserException(DUPLICATE_NICKNAME_ERROR);
        }

        userRepository.save(signUpUser);
    }

    @Override
    public void update(UserUpdateDto userUpdateDto) {
        User wantUpdateUser = getUserByEmail();
        wantUpdateUser.updateUser(userUpdateDto);
    }

    @Override
    public void updatePassword(String checkPassword, String newPassword) {
        User updatePasswordUser = getUserByEmail();

        if (!updatePasswordUser.matchPassword(passwordEncoder, checkPassword)) {
            throw new UserException(INCONSISTENCY_PASSWORD_ERROR);
        }

        updatePasswordUser.updatePassword(passwordEncoder, newPassword);
    }

    @Override
    public void withdraw(String checkPassword) {
        User withdrawUser = userRepository.findByEmail(securityUtil.getLoginUserEmail()).orElseThrow(
                () -> new UserException(ENTITY_NOT_FOUND_ERROR)
        );

        if (!withdrawUser.matchPassword(passwordEncoder, checkPassword)) {
            throw new UserException(INCONSISTENCY_PASSWORD_ERROR);
        }

        userRepository.delete(withdrawUser);
    }

    @Override
    public UserInfoDto getInfo(Long id) {
        if (id == null) {
            throw new UserException(ENTITY_NOT_FOUND_ERROR);
        }

        User findUser = userRepository.findById(id).orElseThrow(
                () ->  new UserException(ENTITY_NOT_FOUND_ERROR)
        );

        return new UserInfoDto(findUser);
    }

    @Override
    public UserInfoDto getMyInfo() {
        User findUser = getUserByEmail();

        return new UserInfoDto(findUser);
    }
}
