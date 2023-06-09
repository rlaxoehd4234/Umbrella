package com.umbrella.project_umbrella.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.umbrella.domain.User.User;
import com.umbrella.domain.exception.UserException;
import com.umbrella.dto.user.UserRequestSignUpDto;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.utils.SecurityUtil;
import com.umbrella.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;

import java.util.*;

import static com.umbrella.domain.exception.UserExceptionType.ENTITY_NOT_FOUND_ERROR;
import static com.umbrella.domain.exception.UserExceptionType.NOT_FOUND_ERROR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SecurityUtil securityUtil;

    ObjectMapper objectMapper = new ObjectMapper();

    private static String SIGNUP_URL = "/signUp";

    private String email = "test@test.com";
    private String password = "codePirates0205!@#";
    private String nickName = "테스트계정";
    private String name = "홍길동";
    private String GENDER = "MALE";
    private String birthDate = "20010304";
    private int age = 23;

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(
                        post(SIGNUP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());
    }

    @Value("${app.auth.cookie.refresh-cookie-key}")
    private String COOKIE_REFRESH_TOKEN_KEY;

    @Value("${jwt.access.header}")
    private String accessHeader;

    private static final String BEARER = "Bearer ";

    private MvcResult login() throws Exception {
        Map<String, String> loginMap = new HashMap<>();

        loginMap.put("email", email);
        loginMap.put("password", password);

        MvcResult result = mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMap))
        ).andExpect(status().isOk()).andReturn();

        return result;
    }

    private String[] getAccessTokenAndRefreshToken() throws Exception {
        MvcResult result = login();
        String refreshToken = result.getResponse().getCookie(COOKIE_REFRESH_TOKEN_KEY).getValue();
        String accessToken = result.getResponse().getHeader(accessHeader);

        return new String[] {refreshToken, accessToken};
    }

    private Cookie setRefreshTokenInCookie() throws Exception {
        String[] tokens = getAccessTokenAndRefreshToken();
        Cookie requestCookie = new Cookie(COOKIE_REFRESH_TOKEN_KEY, tokens[0]);

        return requestCookie;
    }

    private UserRequestSignUpDto createSignUpDto() {
        return new UserRequestSignUpDto(email, nickName, password, name, birthDate, GENDER);
    }

    @Test
    @DisplayName("[SUCCESS]_회원가입")
    public void signUpTest() throws Exception {
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        // when
         signUp(signUpData);

        // then
        User signUpUser = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        );

        assertThat(signUpUser.getEmail()).isEqualTo(email);
        assertThat(signUpUser.getNickName()).isEqualTo(nickName);
        assertThat(signUpUser.matchPassword(passwordEncoder, password)).isTrue();
        assertThat(signUpUser.getName()).isEqualTo(name);
        assertThat(signUpUser.getAge()).isEqualTo(age);
    }

    @Test
    @DisplayName("[FAILED]_필드_없이_회원가입")
    @Disabled
    public void signUpExceptionTest() throws Exception {
        // given, when, then
        assertThrows(UserException.class,
                () -> objectMapper.writeValueAsString(new UserRequestSignUpDto(null, password, name,
                                                                                nickName, birthDate, GENDER)));
        assertThrows(UserException.class,
                () -> objectMapper.writeValueAsString(new UserRequestSignUpDto(email, null, name,
                                                                                nickName, birthDate, GENDER)));
        assertThrows(UserException.class,
                () -> objectMapper.writeValueAsString(new UserRequestSignUpDto(email, password, null,
                                                                                nickName, birthDate, GENDER)));
        assertThrows(UserException.class,
                () -> objectMapper.writeValueAsString(new UserRequestSignUpDto(email, password, name,
                                                                        null, birthDate, GENDER)));
        assertThrows(UserException.class,
                () -> objectMapper.writeValueAsString(new UserRequestSignUpDto(email, password, name,
                                                                                nickName, null, GENDER)));
        assertThrows(UserException.class,
                () -> objectMapper.writeValueAsString(new UserRequestSignUpDto(email, password, name,
                        nickName, birthDate, null)));
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_수정")
    public void updateUserInfoTest() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String[] tokens = getAccessTokenAndRefreshToken();
        String accessToken = tokens[1];
        Map<String, Object> updateUserMap = new HashMap<>();

        updateUserMap.put("name", "임꺽정");
        updateUserMap.put("nick_name", "변경테스트");
        updateUserMap.put("age", age + 1);

        String updateUserData = objectMapper.writeValueAsString(updateUserMap);
        Cookie requestCookie = new Cookie(COOKIE_REFRESH_TOKEN_KEY, tokens[0]);

        // when
        mockMvc.perform(
                put("/user/update/info")
                        .cookie(requestCookie)
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserData)
        ).andExpect(status().isOk());

        // then
        User updateUser = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        );

        assertThat(updateUser.getNickName()).isEqualTo(updateUserMap.get("nick_name"));
        assertThat(updateUser.getName()).isEqualTo(updateUserMap.get("name"));
        assertThat(updateUser.getAge()).isEqualTo(updateUserMap.get("age"));
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_이름_수정")
    public void updateUserNameTest() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];
        Map<String, Object> updateUserMap = new HashMap<>();

        updateUserMap.put("name", "임꺽정");

        String updateUserData = objectMapper.writeValueAsString(updateUserMap);

        // when
        mockMvc.perform(
                put("/user/update/info")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserData)
        ).andExpect(status().isOk());

        // then
        User updateUser = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        );

        assertThat(updateUser.getName()).isEqualTo(updateUserMap.get("name"));
        assertThat(updateUser.getNickName()).isEqualTo(nickName);
        assertThat(updateUser.getAge()).isEqualTo(age);
    }

    @Test
    @DisplayName("[SUCCESS]_비밀번호_수정")
    public void updatePasswordTest() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Map<String, Object> passwordUpdateMap = new HashMap<>();
        passwordUpdateMap.put("checkPassword", password);
        passwordUpdateMap.put("newPassword", password + "!");

        String updatePasswordData = objectMapper.writeValueAsString(passwordUpdateMap);

        // when
        mockMvc.perform(
                patch("/user/update/password")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordData)
        ).andExpect(status().isOk());

        // then
        User updateUser = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        );
        assertThat(passwordEncoder.matches(password, updateUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password + "!", updateUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("[FAILED]_올바르지_않은_형태의_비밀번호_수정")
    public void updatePasswordExceptionTest01() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Map<String, Object> passwordUpdateMap = new HashMap<>();
        passwordUpdateMap.put("checkPassword", password);
        passwordUpdateMap.put("newPassword", "123456789");

        String updatePasswordData = objectMapper.writeValueAsString(passwordUpdateMap);

        // when
        mockMvc.perform(
                patch("/user/update/password")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordData)
        ).andExpect(status().isBadRequest());

        // then
        User updateUser = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        );
        assertThat(passwordEncoder.matches(password, updateUser.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("123456789", updateUser.getPassword())).isFalse();
    }

    @Test
    @DisplayName("[FAILED]_비밀번호_수정_기존_비밀번호_틀림")
    public void updatePasswordExceptionTest02() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Map<String, Object> passwordUpdateMap = new HashMap<>();
        passwordUpdateMap.put("checkPassword", password + "wrong");
        passwordUpdateMap.put("newPassword", "123456789");

        String updatePasswordData = objectMapper.writeValueAsString(passwordUpdateMap);

        // when
        mockMvc.perform(
                patch("/user/update/password")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordData)
        ).andExpect(status().isBadRequest());

        // then
        User updateUser = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        );
        assertThat(passwordEncoder.matches(password, updateUser.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("123456789", updateUser.getPassword())).isFalse();
    }

    @Test
    @DisplayName("[SUCCESS]_회원탈퇴")
    public void withdrawUserTest() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Map<String, Object> passwordMap = new HashMap<>();
        passwordMap.put("password", password);

        String updatePasswordData = objectMapper.writeValueAsString(passwordMap);

        // when
        mockMvc.perform(
                delete("/user/withdraw")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordData)
        ).andExpect(status().isOk());

        // then
        assertThrows(EntityNotFoundException.class, () -> userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        ));
    }

    @Test
    @DisplayName("[FAILED]_회원탈퇴_실패_비밀번호_틀림")
    public void withdrawUserExceptionTest01() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Map<String, Object> passwordCheckeMap = new HashMap<>();
        passwordCheckeMap.put("password", password + "wrong");

        String updatePasswordData = objectMapper.writeValueAsString(passwordCheckeMap);

        // when, then
        mockMvc.perform(
                delete("/user/withdraw")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordData)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[FAILED]_회원탈퇴_실패_권한없음")
    public void withdrawUserExceptionTest02() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Map<String, Object> passwordUpdateMap = new HashMap<>();
        passwordUpdateMap.put("password", password);

        String updatePassword = objectMapper.writeValueAsString(passwordUpdateMap);

        //when
        mockMvc.perform(
                        delete("/user/withdraw")
                                .header(accessHeader,BEARER + accessToken + "wrong")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isForbidden());

        //then
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일을 사용하는 계정이 존재하지 않습니다.")
        );
        assertThat(user).isNotNull();
    }

    @Test
    @DisplayName("[SUCCESS]_회원정보_읽어오기")
    public void getUserInfoTest() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Long userId = userRepository.findAll().get(0).getId();

        // when
        MvcResult result = mockMvc.perform(
                post("/user/" + userId + "/info")
                        .characterEncoding("utf-8")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken)
        ).andExpect(status().isOk()).andReturn();

        result.getResponse().setCharacterEncoding("utf-8");

        // then
        Map<String, Object> readUserInfoMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("회원이 존재하지 않습니다.")
        );

        assertThat(findUser.getNickName()).isEqualTo(readUserInfoMap.get("nick_name"));
        assertThat(findUser.getName()).isEqualTo(readUserInfoMap.get("name"));
        assertThat(findUser.getAge()).isEqualTo(readUserInfoMap.get("age"));
    }

    @Test
    @DisplayName("[FAILED]_존재하지_않는_회원정보_읽어오기")
    public void getUserInfoExceptionTest01() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        // when, then
        MvcResult result = mockMvc.perform(post("/user/" + 9999 + "/info")
                .characterEncoding("utf-8")
                .cookie(setRefreshTokenInCookie())
                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @DisplayName("[FAILED]_만료된_토큰으로_회원정보_읽어오기")
    public void getUserInfoExceptionTest02() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Long userId = userRepository.findAll().get(0).getId();

        // when, then
        mockMvc.perform(
                get("/user/" + userId + "/info")
                        .characterEncoding("utf-8")
                        .header(accessHeader, BEARER + accessToken + "wrong")
        ).andExpect(status().isForbidden()).andReturn() ;
    }

    @Test
    @DisplayName("[FAILED]_만료된_엑세스_토큰으로_갱신하고_회원정보_읽어오기")
    public void getUserInfoTest02() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        Long userId = userRepository.findAll().get(0).getId();

        // when, then
        mockMvc.perform(
                get("/user/" + userId + "/info")
                        .characterEncoding("utf-8")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken + "wrong")
        ).andExpect(status().isForbidden()).andReturn() ;
    }

    @Test
    @DisplayName("[SUCCESS]_내_정보_읽어오기")
    public void getMyInfoTest() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];
        Cookie requestCookie = setRefreshTokenInCookie();
        // when
        MvcResult result = mockMvc.perform(
                get("/user/info")
                        .characterEncoding("utf-8")
                        .cookie(setRefreshTokenInCookie())
                        .header(accessHeader, BEARER + accessToken)
        ).andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        // then
        Map<String, Object> readUserInfoMap = objectMapper.readValue(response.getContentAsString(), Map.class);
        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("회원이 존재하지 않습니다.")
        );

        assertThat(findUser.getNickName()).isEqualTo(readUserInfoMap.get("nick_name"));
        assertThat(findUser.getName()).isEqualTo(readUserInfoMap.get("name"));
        assertThat(findUser.getAge()).isEqualTo(readUserInfoMap.get("age"));
    }

    @Test
    @DisplayName("[FAILED]_내_정보_읽어오기")
    public void getMyInfoExceptionTest() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];

        // when, then
        mockMvc.perform(
                get("/user/info")
                        .characterEncoding("utf-8")
                        .header(accessHeader, BEARER + accessToken + "wrong")
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    @DisplayName("[SUCCESS]_RefreshTokenInCookie_Test")
    public void refreshTokenInCookieTest() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(createSignUpDto());

        signUp(signUpData);

        String accessToken = getAccessTokenAndRefreshToken()[1];
        Cookie refreshTokenInCookie = setRefreshTokenInCookie();

        // when, then
        MvcResult result = mockMvc.perform(
                get("/user/info")
                        .characterEncoding("utf-8")
                        .cookie(refreshTokenInCookie)
                        .header(accessHeader, BEARER + accessToken)
        ).andExpect(status().isOk()).andReturn();
    }
}
