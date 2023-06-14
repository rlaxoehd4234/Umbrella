package com.umbrella.service.Impl;

import com.umbrella.domain.Board.Board;
import com.umbrella.domain.Board.BoardRepository;
import com.umbrella.domain.User.User;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import com.umbrella.domain.WorkSpace.WorkspaceUserRepository;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.WorkspaceException;
import com.umbrella.dto.user.*;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.dto.workspace.WorkspaceRequestCreateDto;
import com.umbrella.dto.workspace.WorkspaceRequestEnterAndExitDto;
import com.umbrella.dto.workspace.WorkspaceResponseDto;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.JwtService;
import com.umbrella.service.LoginService;
import com.umbrella.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.umbrella.domain.exception.UserExceptionType.*;
import static com.umbrella.domain.exception.WorkspaceExceptionType.ALREADY_ENTERED_WORKSPACE_ERROR;
import static com.umbrella.domain.exception.WorkspaceExceptionType.NOT_FOUND_WORKSPACE;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {
    private final BoardRepository boardRepository;

    private final EntityManager entityManager;

    private final UserRepository userRepository;

    private final WorkspaceUserRepository workspaceUserRepository;

    private final WorkSpaceRepository workSpaceRepository;

    private final LoginService loginService;

    private final JwtService jwtService;

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

        return userRepository.save(signUpUser);
    }

    @Override
    public UserResponseLoginDto login(UserRequestLoginDto request, HttpServletResponse response) {
        String email = request.getEmail();
        String password = request.getPassword();

        UserContext theUser = (UserContext) loginService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, theUser.getPassword())) {
            throw new UserException(UNMATCHED_LOGIN_INFO_ERROR);
        }

        String accessToken = jwtService.createAccessToken(email, theUser.getNickName());
//        String refreshToken = jwtService.createRefreshToken(email);

//        userRepository.findByEmail(email).ifPresent(user -> user.updateRefreshToken(refreshToken));

//        jwtService.setRefreshTokenInCookie(response, refreshToken);
        jwtService.sendAccessToken(response, accessToken);

        logSuccess(email, accessToken);

        return UserResponseLoginDto.builder()
                                        .userId(theUser.getId())
                                        .nickName(theUser.getNickName())
                                        .email(email)
                                        .build();
    }

    private void logSuccess(String email, String accessToken) {
        log.info( "로그인에 성공합니다. email: {}", email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}", accessToken);
//        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);
    }

    @Override
    public UserResponseUpdateDto update(UserRequestUpdateDto userUpdateDto) {
        User wantUpdateUser = getLoginUserByEmail();
        wantUpdateUser.updateUser(userUpdateDto);

        return UserResponseUpdateDto.builder()
                .nickName(wantUpdateUser.getNickName())
                .name(wantUpdateUser.getName())
                .age(wantUpdateUser.getAge())
                .build();
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

        Board board = Board.builder().
                        title("공지 사항").
                        workSpace(theWorkspace).build();

        WorkspaceUser theWorkspaceUser = new WorkspaceUser();

        entityManager.persist(theWorkspace);
        boardRepository.save(board);
        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace);
        workspaceUserRepository.save(theWorkspaceUser);

        return theWorkspace.getId();
    }

    @Override
    public WorkspaceResponseDto enterWorkspace(WorkspaceRequestEnterAndExitDto workspaceRequestEnterDto) {
        User theUser = getLoginUserByEmail();
        if (theUser.getWorkspaceUsers().stream().anyMatch( workspaceUser ->
                workspaceUser.getWorkspace().getId() == workspaceRequestEnterDto.getId() &&
                workspaceUser.getWorkspace().getTitle().equals(workspaceRequestEnterDto.getTitle()))) {
            throw new WorkspaceException(ALREADY_ENTERED_WORKSPACE_ERROR);
        }
        Optional<WorkSpace> existWorkspace = workSpaceRepository
                .findByIdAndTitle(workspaceRequestEnterDto.getId(), workspaceRequestEnterDto.getTitle());
        if (existWorkspace.isEmpty()) {
            throw new WorkspaceException(NOT_FOUND_WORKSPACE);
        }

        WorkSpace theWorkspace = existWorkspace.get();

        WorkspaceUser theWorkspaceUser = new WorkspaceUser();
        theUser.enterWorkspaceUser(theWorkspaceUser);
        theWorkspaceUser.takeWorkspace(theWorkspace);

        workspaceUserRepository.save(theWorkspaceUser);

        return new WorkspaceResponseDto(theWorkspace);
    }

    @Override
    public void exitWorkspace(WorkspaceRequestEnterAndExitDto workspaceRequestEnterAndExitDto) {
        User theUser = getLoginUserByEmail();
        WorkspaceUser theWorkspaceUser = theUser.getWorkspaceUsers().stream().filter(workspaceUser ->
                workspaceUser.getWorkspace().getId() == (workspaceRequestEnterAndExitDto.getId()) &&
                workspaceUser.getWorkspace().getTitle().equals(workspaceRequestEnterAndExitDto.getTitle())).findFirst()
                .orElseThrow( () -> new EntityNotFoundException("해당 워크스페이스를 찾을 수 없습니다!")
        );

        theUser.exitWorkspaceUser(theWorkspaceUser);

        if (theWorkspaceUser != null) {
            theUser.exitWorkspaceUser(theWorkspaceUser);
            workspaceUserRepository.delete(theWorkspaceUser);
        }
    }
}
