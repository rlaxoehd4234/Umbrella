package com.umbrella.security.login.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Transactional
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Long userId = getUserIdFromAuthentication(authentication);
        String email = getEmailFromAuthentication(authentication);
        String nickName = getNickNameFromAuthentication(authentication);

        String accessToken = jwtService.createAccessToken(email, nickName);
        String refreshToken = jwtService.createRefreshToken(email);

        jwtService.setRefreshTokenInCookie(response, refreshToken);
        jwtService.sendAccessToken(response, accessToken);

        userRepository.findByEmail(email).ifPresent(user -> user.updateRefreshToken(refreshToken));

        String responseBody = createLoginSuccessResponse(userId, nickName);

        sendResponse(response, responseBody);

        logSuccess(email, accessToken, refreshToken);
    }

    private void logSuccess(String email, String accessToken, String refreshToken) {
        log.info( "로그인에 성공합니다. email: {}", email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}", accessToken);
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);
    }

    private void sendResponse(HttpServletResponse response, String responseBody) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(responseBody);
        response.getWriter().flush();
        response.getWriter().close();
    }

    private String createLoginSuccessResponse(Long userId, String nickName) throws JsonProcessingException {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user_id", userId);
        responseMap.put("nick_name", nickName);
        String responseBody = objectMapper.writeValueAsString(responseMap) + "\n성공적으로 로그인이 완료되었습니다!";
        return responseBody;
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        return ((UserContext) authentication.getPrincipal()).getId();
    }

    private String getEmailFromAuthentication(Authentication authentication) {
        return ((UserContext) authentication.getPrincipal()).getUsername();
    }

    private String getNickNameFromAuthentication(Authentication authentication) {
        return ((UserContext) authentication.getPrincipal()).getNickName();
    }
}

