package com.umbrella.service.Impl;

import com.umbrella.domain.User.User;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import com.umbrella.domain.WorkSpace.WorkspaceUserRepository;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.WorkspaceException;
import com.umbrella.dto.user.UserInfoDto;
import com.umbrella.dto.user.UserRequestFindPasswordDto;
import com.umbrella.dto.user.UserRequestSignUpDto;
import com.umbrella.dto.user.UserRequestUpdateDto;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.dto.workspace.WorkspaceRequestCreateDto;
import com.umbrella.dto.workspace.WorkspaceRequestEnterAndExitDto;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import java.util.Optional;

import static com.umbrella.domain.exception.UserExceptionType.*;
import static com.umbrella.domain.exception.WorkspaceExceptionType.ALREADY_ENTERED_WORKSPACE_ERROR;
import static com.umbrella.domain.exception.WorkspaceExceptionType.NOT_FOUNT_WORKSPACE;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {

    private final EntityManager entityManager;

    private final UserRepository userRepository;

    private final WorkspaceUserRepository workspaceUserRepository;

    private final WorkSpaceRepository workSpaceRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityUtil securityUtil;

    private User userSignUpDtoToEntity(UserRequestSignUpDto userRequestSignUpDto) {

        return User.builder()
                .email(userRequestSignUpDto.getEmail())
                .nickName(userRequestSignUpDto.getNickName())
                .password(userRequestSignUpDto.getPassword())
                .name(userRequestSignUpDto.getName())
                .age(userRequestSignUpDto.calculateAge())
                .gender(userRequestSignUpDto.getGender())
                .build();
    }

    private User getLoginUserByEmail() {
        return userRepository.findByEmail(securityUtil.getLoginUserEmail()).orElseThrow(
                () -> new UserException(ENTITY_NOT_FOUND_ERROR)
        );
    }

    private void checkValidatePassword(String checkPassword, User withdrawUser) {
        if (!withdrawUser.matchPassword(passwordEncoder, checkPassword)) {
            throw new UserException(INCONSISTENCY_PASSWORD_ERROR);
        }
    }

    @Override
    public User signUp(UserRequestSignUpDto userSignUpDto) {
        User signUpUser = userSignUpDtoToEntity(userSignUpDto);

        signUpUser.addUserAuthorities();
        signUpUser.addDefaultPlatform();
        signUpUser.encodePassword(passwordEncoder);

        String email = userSignUpDto.getEmail();
        String nickName = userSignUpDto.getNickName();

        Optional<User> optionalUser = userRepository.findByEmailOrNickName(email, nickName);
        optionalUser.ifPresent(user -> {
            if (user.getEmail().equals(email)) {
                throw new UserException(DUPLICATE_EMAIL_ERROR);
            } else if (user.getNickName().equals(nickName)) {
                throw new UserException(DUPLICATE_NICKNAME_ERROR);
            }
        });

        return  userRepository.save(signUpUser);
    }

    @Override
    public void update(UserRequestUpdateDto userUpdateDto) {
        User wantUpdateUser = getLoginUserByEmail();
        wantUpdateUser.updateUser(userUpdateDto);
    }

    @Override
    public void updatePassword(String checkPassword, String newPassword) {
        User updatePasswordUser = getLoginUserByEmail();

        checkValidatePassword(checkPassword, updatePasswordUser);

        updatePasswordUser.updatePassword(passwordEncoder, newPassword);
    }

    @Override
    public void reCreatePassword(UserRequestFindPasswordDto userRequestFindPasswordDto) {
        User theUser = getLoginUserByEmail();
        theUser.updatePassword(passwordEncoder, userRequestFindPasswordDto.getPassword());
    }

    @Override
    public void withdraw(String checkPassword) {
        User withdrawUser = getLoginUserByEmail();
        checkValidatePassword(checkPassword, withdrawUser);
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
        User findUser = getLoginUserByEmail();
        return new UserInfoDto(findUser);
    }

    private WorkSpace workspaceDtoToEntity(WorkspaceRequestCreateDto workspaceCreateDto) {

        return WorkSpace.builder()
                .title(workspaceCreateDto.getTitle())
                .description(workspaceCreateDto.getDescription())
                .build();
    }

    @Override
    public Long createWorkspace(WorkspaceRequestCreateDto workspaceCreateDto) {
        User theUser = getLoginUserByEmail();
        WorkSpace theWorkspace = workspaceDtoToEntity(workspaceCreateDto);

        WorkspaceUser theWorkspaceUser = new WorkspaceUser();
        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace);
        workspaceUserRepository.save(theWorkspaceUser);

        return theWorkspace.getId();
    }

    @Override
    public void enterWorkspace(WorkspaceRequestEnterAndExitDto workspaceRequestEnterDto) {
        User theUser = getLoginUserByEmail();
        if (theUser.getWorkspaceUsers().stream().anyMatch( workspaceUser -> {
            return workspaceUser.getWorkspace().getId() == workspaceRequestEnterDto.getId() &&
                    workspaceUser.getWorkspace().getTitle().equals(workspaceRequestEnterDto.getTitle());
        } )) {
            throw new WorkspaceException(ALREADY_ENTERED_WORKSPACE_ERROR);
        }
        Optional<WorkSpace> theWorkspace = workSpaceRepository
                .findByIdAndTitle(workspaceRequestEnterDto.getId(), workspaceRequestEnterDto.getTitle());
        if (theWorkspace.isEmpty()) {
            throw new WorkspaceException(NOT_FOUNT_WORKSPACE);
        }

        entityManager.persist(theUser);
        entityManager.persist(theWorkspace.get());

        WorkspaceUser theWorkspaceUser = new WorkspaceUser();
        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace.get());

        workspaceUserRepository.save(theWorkspaceUser);
    }

    @Override
    public void exitWorkspace(WorkspaceRequestEnterAndExitDto workspaceRequestEnterAndExitDto) {
        User theUser = getLoginUserByEmail();
        WorkspaceUser theWorkspaceUser = theUser.getWorkspaceUsers().stream().filter(workspaceUser -> {
            return workspaceUser.getWorkspace().getId() == (workspaceRequestEnterAndExitDto.getId()) &&
                    workspaceUser.getWorkspace().getTitle().equals(workspaceRequestEnterAndExitDto.getTitle());
        }).findFirst().orElseThrow(
                () -> new EntityNotFoundException("해당 워크스페이스를 찾을 수 없습니다!")
        );

        theUser.exitWorkspaceUser(theWorkspaceUser);

        if (theWorkspaceUser != null) {
            theUser.exitWorkspaceUser(theWorkspaceUser);
            workspaceUserRepository.delete(theWorkspaceUser);
        }
    }
}
