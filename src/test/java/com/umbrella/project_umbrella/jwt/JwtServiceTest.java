package com.umbrella.project_umbrella.jwt;

import com.umbrella.constant.Gender;
import com.umbrella.constant.Role;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class JwtServiceTest {

    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;
    @Value("${app.auth.cookie.refresh-cookie-key}")
    private String COOKIE_REFRESH_TOKEN_KEY;

    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private String email = "test@test.com";
    private String nickName = "테스트계정";
    private String password = "12345";
    @BeforeEach
    public void init() {
        User user = User.builder()
                        .email(email)
                        .password(password)
                        .name("홍길동")
                        .nickName(nickName)
                        .age(22)
                        .gender(Gender.MALE)
                        .role(Role.USER)
                .build();

        userRepository.save(user);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("[SUCCESS]_엑세스_토큰_발급")
    public void createAccessTokenTest() {
        // given
        String accessToken = jwtService.createAccessToken(email, nickName);

        // when
        String findEmail = jwtService.extractEmail(accessToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );

        String findSubject = jwtService.extractSubject(accessToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );;

        // then
        assertThat(findEmail).isEqualTo(email);
        assertThat(findSubject).isEqualTo(email);
    }

    @Test
    @DisplayName("[SUCCESS]_리프레쉬_토큰_발급")
    public void createRefreshTokenTest() {
        // given
        String refreshToken = jwtService.createRefreshToken(email);

        // when
        String findSubject = jwtService.extractSubject(refreshToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );;

        // then
        assertThrows(JwtException.class, () -> jwtService.extractEmail(refreshToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        ));
        assertThat(findSubject).isEqualTo(email);
    }

    @Test
    @DisplayName("[SUCCESS]_리프레쉬_토큰_갱신")
    public void updateRefreshTokenTest() throws InterruptedException {
        // given
        String refreshToken = jwtService.createRefreshToken(email);
        jwtService.updateRefreshToken(email, refreshToken);

        em.flush();
        em.clear();

        Thread.sleep(3000);

        // when
        String reIssuedRefreshToken = jwtService.createRefreshToken(email);
        jwtService.updateRefreshToken(email, reIssuedRefreshToken);

        em.flush();
        em.clear();

        // then
        assertThrows(Exception.class, () -> userRepository.findByRefreshToken(refreshToken).get());
        assertThat(userRepository.findByRefreshToken(reIssuedRefreshToken).get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("[SUCCESS]_리프레쉬_토큰_제거")
    public void destroyRefreshToken() {
        // given
        String refreshToken = jwtService.createRefreshToken(email);
        jwtService.updateRefreshToken(email, refreshToken);

        em.flush();
        em.clear();

        // when
        jwtService.destroyRefreshToken(email);

        em.flush();
        em.clear();

        // then
        assertThrows(Exception.class, () -> userRepository.findByRefreshToken(refreshToken).get());

        User user = userRepository.findByEmail(email).get();
        assertThat(user.getRefreshToken()).isNull();
    }

    @Test
    @DisplayName("[SUCCESS_엑세스_토큰_헤더_설정")
    public void setAccessHeaderTest() throws IOException {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(email, nickName);
        String refreshToken = jwtService.createRefreshToken(email);

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);

        // when
//        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken); // RefreshToken In Json Body
        jwtService.setRefreshTokenInCookie(mockHttpServletResponse, refreshToken);
        jwtService.sendAccessToken(mockHttpServletResponse, accessToken);

        // then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("[SUCCESS_리프레쉬_토큰_헤더_설정")
    public void setRefreshHeaderTest() throws IOException {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(email, nickName);
        String refreshToken = jwtService.createRefreshToken(email);

//        jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);  // RefreshToken In Json Body

        // when
//        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken); // RefreshToken In Json Body
        jwtService.setRefreshTokenInCookie(mockHttpServletResponse, refreshToken);
        jwtService.sendAccessToken(mockHttpServletResponse, accessToken);

        // then
        String headerRefreshToken = mockHttpServletResponse.getHeader(COOKIE_REFRESH_TOKEN_KEY);

//        assertThat(headerRefreshToken).isEqualTo(refreshToken);   // RefreshToken In Json Body
        assertThat(headerRefreshToken).isNull();
        assertThat(refreshToken).isEqualTo(mockHttpServletResponse.getCookie(COOKIE_REFRESH_TOKEN_KEY).getValue());
    }

    @Test
    @DisplayName("[SUCCESS]_토큰_전송")
    public void sendAccessAndRefreshTokenTest() throws IOException {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(email, nickName);
        String refreshToken = jwtService.createRefreshToken(email);

        // when
//        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken); // RefreshToken In Json Body
        jwtService.setRefreshTokenInCookie(mockHttpServletResponse, refreshToken);
        jwtService.sendAccessToken(mockHttpServletResponse, accessToken);

        // then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
//        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader); // RefreshToken In Json Body
        String refreshTokenInCookie = mockHttpServletResponse.getCookie(COOKIE_REFRESH_TOKEN_KEY).getValue();

        assertThat(headerAccessToken).isEqualTo(accessToken);
        assertThat(refreshTokenInCookie).isEqualTo(refreshToken);
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
//        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse,accessToken,refreshToken); // RefreshToken In Json Body
        jwtService.setRefreshTokenInCookie(mockHttpServletResponse, refreshToken);
        jwtService.sendAccessToken(mockHttpServletResponse, accessToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
//        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader); // RefreshToken In Json Body
        Cookie refreshTokenInCookie = mockHttpServletResponse.getCookie(COOKIE_REFRESH_TOKEN_KEY);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER + headerAccessToken);
        httpServletRequest.addHeader(HttpHeaders.SET_COOKIE, refreshTokenInCookie);

        return httpServletRequest;
    }

    @Test
    @DisplayName("[SUCCESS]_엑세스_토큰_추출")
    public void extractAccessTokenTest() throws IOException, ServletException {
        // given
        String accessToken = jwtService.createAccessToken(email, nickName);
        String refreshToken = jwtService.createRefreshToken(email);

        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        // when
        String extractedAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );

        // then
        String extractEmail = jwtService.extractEmail(accessToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );

        assertThat(extractedAccessToken).isEqualTo(accessToken);
        assertThat(extractEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("[SUCCESS]_리프레쉬_토큰_추출")
    @Disabled
    public void extractRefreshTokenTest() throws IOException, ServletException {
        // given
        String accessToken = jwtService.createAccessToken(email, nickName);
        String refreshToken = jwtService.createRefreshToken(email);

        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        // when
        String extractedRefreshToken = String.valueOf(jwtService.extractRefreshToken(httpServletRequest));

        // then
        assertThat(extractedRefreshToken).isEqualTo(refreshToken);
        assertThat(jwtService.extractEmail(refreshToken)).isEmpty();
    }

    @Test
    @DisplayName("[SUCCESS]_엑세스_토큰_클레임_추출")
    public void extractAccessTokenClaimsTest() throws IOException, ServletException {
        // given
        String accessToken = jwtService.createAccessToken(email, nickName);
        String refreshToken = jwtService.createRefreshToken(email);

        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String requestAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );

        // when
        String extractEmail = jwtService.extractEmail(requestAccessToken).orElseThrow(
                () -> new JwtException("유효하지 않은 토큰입니다.")
        );

        // then
        assertThat(extractEmail).isEqualTo(email);
    }
}
