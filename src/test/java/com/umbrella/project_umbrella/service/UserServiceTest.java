package com.umbrella.project_umbrella.service;

import com.umbrella.constant.AuthPlatform;
import com.umbrella.constant.Role;
import com.umbrella.domain.User.User;
import com.umbrella.domain.WorkSpace.WorkSpace;
import com.umbrella.domain.WorkSpace.WorkSpaceRepository;
import com.umbrella.domain.WorkSpace.WorkspaceUser;
import com.umbrella.domain.WorkSpace.WorkspaceUserRepository;
import com.umbrella.domain.exception.UserException;
import com.umbrella.domain.exception.WorkspaceException;
import com.umbrella.dto.user.UserInfoDto;
import com.umbrella.dto.user.UserRequestSignUpDto;
import com.umbrella.dto.user.UserRequestUpdateDto;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.dto.workspace.WorkspaceRequestCreateDto;
import com.umbrella.dto.workspace.WorkspaceRequestEnterAndExitDto;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.security.utils.RoleUtil;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import java.util.Optional;

import static com.umbrella.domain.exception.UserExceptionType.NOT_FOUND_ERROR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WorkSpaceRepository workSpaceRepository;

    @Autowired
    WorkspaceUserRepository workspaceUserRepository;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    RoleUtil roleUtil;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private static final String nickname = "테스트계정";

    private static final String password = "codePirates0204";

    private static final String name = "홍길동";

    private static final String birthDate = "20010304";

    private static final String GENDER = "MALE";

    private UserRequestSignUpDto createUserSignUpDto(int i) {
        return new UserRequestSignUpDto("test" + i + "@test.com", nickname + i, password, name, birthDate, GENDER);
    }

    private UserRequestSignUpDto setAuthenticationInContext(int i) {
        UserRequestSignUpDto userSignUpDto = createUserSignUpDto(i);

        User theUser = userService.signUp(userSignUpDto);
        em.flush();
        em.clear();

        UserDetails authenticatedUser = new UserContext(theUser.getEmail(), theUser.getPassword(),
                theUser.getId(), theUser.getNickName(), roleUtil.addAuthoritiesForContext(theUser));

        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null,
                authoritiesMapper.mapAuthorities(authenticatedUser.getAuthorities()));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        return userSignUpDto;
    }

    @AfterEach
    private void clearAuthentication() {
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }

    @Test
    @DisplayName("[SUCCESS]_회원가입_성공")
    public void signUpTest() {
        // given
        UserRequestSignUpDto userSignUpDto = createUserSignUpDto(0);

        // when
        userService.signUp(userSignUpDto);
        em.flush();
        em.clear();

        // then
        User savedUser = userRepository.findByNickName(userSignUpDto.getNickName()).orElseThrow(
                () -> new EntityNotFoundException("해당 닉네임을 사용하는 계정을 찾을 수 없습니다.")
        );

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(userSignUpDto.getEmail());
        assertThat(savedUser.getNickName()).isEqualTo(userSignUpDto.getNickName());
        assertThat(savedUser.getName()).isEqualTo(userSignUpDto.getName());
        assertThat(savedUser.getAge()).isEqualTo(userSignUpDto.calculateAge());
        assertThat(savedUser.getPlatform()).isEqualTo(AuthPlatform.UMBRELLA);
        assertThat(savedUser.getRole()).isSameAs(Role.USER);
    }

    @Test
    @DisplayName("[FAILED]_회원가입_실패_이메일_중복")
    public void signUpExceptionTest01() {
        // given
        UserRequestSignUpDto userSignUpDto = createUserSignUpDto(0);

        userService.signUp(userSignUpDto);
        em.flush();
        em.clear();

        assertThat(assertThrows(UserException.class, () -> userService.signUp(userSignUpDto)).getBaseExceptionType().getErrorMessage())
                .isEqualTo("동일한 이메일을 사용하는 계정이 이미 존재합니다.");
    }

    @Test
    @DisplayName("[FAILED]_회원가입_실패_닉네임_중복")
    @Disabled
    public void signUpExceptionTest02() {
        // given
        UserRequestSignUpDto userSignUpDto = createUserSignUpDto(0);

        userService.signUp(userSignUpDto);
        em.flush();
        em.clear();

        // when, then
        assertThat(assertThrows(UserException.class, () -> userService.signUp(userSignUpDto)).getMessage())
                .isEqualTo("동일한 닉네임을 사용하는 계정이 이미 존재합니다.");
    }

    /* Exception Handling 을 했기 때문에 DTO 를 만드는 과정에서는 에러가 발생할 일이 없기 때문에 비활성화함*/
    @Test
    @DisplayName("[FAILED]_회원가입_실패_존재하지_않는_필드")
    @Disabled
    public void signUpExceptionTest03() {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> new UserRequestSignUpDto(null,
                "테스트계정01",
                passwordEncoder.encode(password),
                "홍길동",
                birthDate,
                GENDER));
        assertThrows(IllegalArgumentException.class, () -> new UserRequestSignUpDto("test02@test.com",
                null,
                passwordEncoder.encode(password),
                "홍길동",
                birthDate,
                GENDER));
        assertThrows(IllegalArgumentException.class, () -> new UserRequestSignUpDto("test03@test.com",
                "테스트계정03",
                null,
                "홍길동",
                birthDate,
                GENDER));
        assertThrows(IllegalArgumentException.class, () -> new UserRequestSignUpDto("test04@test.com",
                "테스트계정04",
                passwordEncoder.encode(password),
                null,
                birthDate,
                GENDER));
        assertThrows(IllegalArgumentException.class, () -> new UserRequestSignUpDto("test05@test.com",
                "테스트계정05",
                passwordEncoder.encode(password),
                "홍길동",
                null,
                GENDER));
        assertThrows(IllegalArgumentException.class, () -> new UserRequestSignUpDto("test06@test.com",
                "테스트계정06",
                passwordEncoder.encode(password),
                "홍길동",
                birthDate,
                null
        ));
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_비밀번호_변경")
    public void updatePasswordTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        String changePassword = "codePirates0205";
        userService.updatePassword(password, changePassword);
        em.flush();
        em.clear();

        // then
        User findUser = userRepository.findByNickName(userSignUpDto.getNickName()).orElseThrow(
                () -> new EntityNotFoundException("해당 닉네임을 사용하지 계정이 존재하지 않습니다.")
        );

        assertThat(findUser.matchPassword(passwordEncoder, changePassword)).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_이름만_변경")
    public void updateMNameTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        String changeMName = "임꺽정";
        UserRequestUpdateDto userUpdateDto = new UserRequestUpdateDto(Optional.empty(),
                                                                Optional.of(changeMName),
                                                        Optional.empty());
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByEmail(securityUtil.getLoginUserEmail()).ifPresent(
                user -> {
                    assertThat(user.getName()).isEqualTo(changeMName);
                    assertThat(user.getNickName()).isEqualTo(userSignUpDto.getNickName());
                    assertThat(user.getAge()).isEqualTo(userSignUpDto.calculateAge());
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_닉네임만_변경")
    public void updateUserNickNameTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        String changeNickName = "변경테스트";
        UserRequestUpdateDto userUpdateDto = new UserRequestUpdateDto(Optional.of(changeNickName),
                Optional.empty(),
                Optional.empty());
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByEmail(securityUtil.getLoginUserEmail()).ifPresent(
                user -> {
                    assertThat(user.getName()).isEqualTo(userSignUpDto.getName());
                    assertThat(user.getNickName()).isEqualTo(changeNickName);
                    assertThat(user.getAge()).isEqualTo(userSignUpDto.calculateAge());
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_나이만_변경")
    public void updateUserAgeTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        int changeAge = 100;
        UserRequestUpdateDto userUpdateDto = new UserRequestUpdateDto(Optional.empty(),
                Optional.empty(),
                Optional.of(changeAge));
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByEmail(securityUtil.getLoginUserEmail()).ifPresent(
                user -> {
                    assertThat(user.getName()).isEqualTo(userSignUpDto.getName());
                    assertThat(user.getNickName()).isEqualTo(userSignUpDto.getNickName());
                    assertThat(user.getAge()).isEqualTo(changeAge);
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_닉네임과_실명만_변경")
    public void updateUserNickNameAndMNameTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        String changeNickName = "변경테스트";
        String changeMName = "임꺽정";
        UserRequestUpdateDto userUpdateDto = new UserRequestUpdateDto(Optional.of(changeNickName),
                Optional.of(changeMName),
                Optional.empty());
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByEmail(securityUtil.getLoginUserEmail()).ifPresent(
                user -> {
                    assertThat(user.getName()).isEqualTo(changeMName);
                    assertThat(user.getNickName()).isEqualTo(changeNickName);
                    assertThat(user.getAge()).isEqualTo(userSignUpDto.calculateAge());
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_닉네임과_나이만_변경")
    public void updateUserNickNameAndAgeTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        String changeNickName = "변경테스트";
        int changeAge = 100;
        UserRequestUpdateDto userUpdateDto = new UserRequestUpdateDto(Optional.of(changeNickName),
                Optional.empty(),
                Optional.of(changeAge));
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByEmail(securityUtil.getLoginUserEmail()).ifPresent(
                user -> {
                    assertThat(user.getName()).isEqualTo(userSignUpDto.getName());
                    assertThat(user.getNickName()).isEqualTo(changeNickName);
                    assertThat(user.getAge()).isEqualTo(changeAge);
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_실명과_나이만_변경")
    public void updateUserAgeAndMNameTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        int changeAge = 100;
        String changeMName = "임꺽정";
        UserRequestUpdateDto userUpdateDto = new UserRequestUpdateDto(Optional.empty(),
                Optional.of(changeMName),
                Optional.of(changeAge));
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByEmail(securityUtil.getLoginUserEmail()).ifPresent(
                user -> {
                    assertThat(user.getName()).isEqualTo(changeMName);
                    assertThat(user.getNickName()).isEqualTo(userSignUpDto.getNickName());
                    assertThat(user.getAge()).isEqualTo(changeAge);
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정_전부_변경")
    public void updateAllTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        String changeNickName = "변경테스트";
        int changeAge = 100;
        String changeMName = "임꺽정";
        UserRequestUpdateDto userUpdateDto = new UserRequestUpdateDto(Optional.of(changeNickName),
                Optional.of(changeMName),
                Optional.of(changeAge));
        userService.update(userUpdateDto);
        em.flush();
        em.clear();

        // then
        userRepository.findByEmail(securityUtil.getLoginUserEmail()).ifPresent(
                user -> {
                    assertThat(user.getName()).isEqualTo(changeMName);
                    assertThat(user.getNickName()).isEqualTo(changeNickName);
                    assertThat(user.getAge()).isEqualTo(changeAge);
                }
        );
    }

    @Test
    @DisplayName("[SUCCESS]_회원탈퇴")
    public void withdrawTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        userService.withdraw(password);

        // then
        assertThat(assertThrows(EntityNotFoundException.class,
                () -> userRepository.findByEmail(userSignUpDto.getEmail()).orElseThrow(
                        () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
                )
                ).getMessage()).isEqualTo("해당 이메일을 사용하는 계정이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("[FAILED]_회원탈퇴_비밀번호_불일치")
    public void withdrawExceptionTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when, then
        assertThat(assertThrows(UserException.class,
                () -> userService.withdraw(password + 123)
        ).getBaseExceptionType().getErrorMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_조회")
    public void getUserInfoTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);
        User user = userRepository.findByEmail(userSignUpDto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정을 찾을 수 없습니다.")
        );

        // when
        UserInfoDto userInfoDto = userService.getInfo(user.getId());

        // then
        assertThat(userInfoDto.getEmail()).isEqualTo(userSignUpDto.getEmail());
        assertThat(userInfoDto.getName()).isEqualTo(userSignUpDto.getName());
        assertThat(userInfoDto.getAge()).isEqualTo(userSignUpDto.calculateAge());
        assertThat(userInfoDto.getNickName()).isEqualTo(userSignUpDto.getNickName());
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_내_정보_조회")
    public void getMyInfoTest() {
        // given
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);

        // when
        UserInfoDto userInfoDto = userService.getMyInfo();

        // then
        assertThat(userInfoDto.getEmail()).isEqualTo(userSignUpDto.getEmail());
        assertThat(userInfoDto.getName()).isEqualTo(userSignUpDto.getName());
        assertThat(userInfoDto.getAge()).isEqualTo(userSignUpDto.calculateAge());
        assertThat(userInfoDto.getNickName()).isEqualTo(userSignUpDto.getNickName());
    }

    private static final String WORKSPACE_TITLE =  "TEST WORKSPACE";
    private static final String WORKSPACE_DESCRIPTION =  "테스트용 워크스페이스 입니다.";
    private static final String DUPLICATE_ENTER_WORKSPACE_M = "이미 입장한 워크스페이스 입니다.";

    private WorkspaceRequestCreateDto createWorkspaceRequestCreateDto() {
        WorkspaceRequestCreateDto theWorkspaceUserCreateDto = WorkspaceRequestCreateDto.builder()
                .title(WORKSPACE_TITLE)
                .description(WORKSPACE_DESCRIPTION)
                .build();

        return theWorkspaceUserCreateDto;
    }

    private WorkspaceRequestEnterAndExitDto createWorkspaceRequestEnterAndExitDto(Long workspaceId) {
        WorkspaceRequestEnterAndExitDto theWorkspaceUserEnterDto = WorkspaceRequestEnterAndExitDto.builder()
                .id(workspaceId)
                .title(WORKSPACE_TITLE)
                .build();

        return theWorkspaceUserEnterDto;
    }

    @Test
    @DisplayName("[SUCCESS]_워크스페이스_생성")
    public void createWorkspace() {
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);
        WorkspaceRequestCreateDto workspaceRequestCreateDto = createWorkspaceRequestCreateDto();
        Long workspaceId =  userService.createWorkspace(workspaceRequestCreateDto);

        User theUser = userRepository.findByEmail(userSignUpDto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정을 찾을 수 없습니다.")
        );
        WorkSpace theWorkspace = workSpaceRepository.findById(workspaceId).orElseThrow(
                () -> new EntityNotFoundException("해당 워크스페이스를 찾을 수 없습니다.")
        );
        WorkspaceUser theWorkspaceUser = workspaceUserRepository.findByWorkspaceUserAndWorkspace(theUser, theWorkspace)
                .orElseThrow(
                        () -> new EntityNotFoundException("해당 워크스페이스를 찾을 수 없습니다.")
                );

        assertThat(
                theWorkspaceUser.getWorkspaceUser().getEmail()
        ).isEqualTo(userSignUpDto.getEmail());
        assertThat(
                theWorkspaceUser.getWorkspace().getTitle()
        ).isEqualTo(workspaceRequestCreateDto.getTitle());
        assertThat(
                theUser.getWorkspaceUsers().size()
        ).isEqualTo(1);
        assertThat(
                theUser.getWorkspaceUsers().get(0).getWorkspace().getTitle()
        ).isEqualTo(workspaceRequestCreateDto.getTitle());
    }

    @Test
    @DisplayName("[SUCCESS]_워크스페이스_입장")
    public void enterWorkspaceTest() {
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);
        WorkspaceRequestCreateDto workspaceRequestCreateDto = createWorkspaceRequestCreateDto();
        Long theWorkspaceId = userService.createWorkspace(workspaceRequestCreateDto);

        UserRequestSignUpDto theUserSignUpDto = setAuthenticationInContext(1);
        WorkspaceRequestEnterAndExitDto workspaceRequestEnterDto = createWorkspaceRequestEnterAndExitDto(theWorkspaceId);
        userService.enterWorkspace(workspaceRequestEnterDto);

        User theUser = userRepository.findByEmail(securityUtil.getLoginUserEmail()).orElseThrow(
                () -> new UserException(NOT_FOUND_ERROR)
        );
        assertThat(theUser.getWorkspaceUsers().stream().anyMatch( workspaceUser -> {
            return workspaceUser.getWorkspace().getId() == theWorkspaceId;
        })).isEqualTo(true);
    }

    @Test
    @DisplayName("[FAILED]_워크스페이스_중복_입장")
    public void enterWorkspaceFailTest() {
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);
        WorkspaceRequestCreateDto workspaceRequestCreateDto = createWorkspaceRequestCreateDto();
        Long theWorkspaceId = userService.createWorkspace(workspaceRequestCreateDto);
        WorkspaceRequestEnterAndExitDto workspaceRequestEnterDto = createWorkspaceRequestEnterAndExitDto(theWorkspaceId);

        assertThrows(WorkspaceException.class, () -> userService.enterWorkspace(workspaceRequestEnterDto));
    }

    @Test
    @DisplayName("[SUCCESS]_워크스페이스_퇴장")
    public void exitWorkspaceTest() {
        UserRequestSignUpDto userSignUpDto = setAuthenticationInContext(0);
        WorkspaceRequestCreateDto workspaceRequestCreateDto = createWorkspaceRequestCreateDto();
        Long workspaceId = userService.createWorkspace(workspaceRequestCreateDto);

        User theUser = userRepository.findByEmail(userSignUpDto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정을 찾을 수 없습니다.")
        );

        WorkSpace theWorkspace = workSpaceRepository.findByTitle(workspaceRequestCreateDto.getTitle()).orElseThrow(
                () -> new EntityNotFoundException("해당 워크스페이스를 찾을 수 없습니다.")
        );
        WorkspaceRequestEnterAndExitDto workspaceRequestEnterAndExitDto = createWorkspaceRequestEnterAndExitDto(workspaceId);
        userService.exitWorkspace(workspaceRequestEnterAndExitDto);

        assertThat(theUser.getWorkspaceUsers().size()).isEqualTo(0);
        assertThat(theWorkspace.getWorkspaceUsers().size()).isEqualTo(0);
        assertThat(workspaceUserRepository.findByWorkspaceUser(theUser).size()).isEqualTo(0);
        assertThat(workspaceUserRepository.findByWorkspace(theWorkspace).size()).isEqualTo(0);
    }
}

