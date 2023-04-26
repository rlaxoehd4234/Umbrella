package com.umbrella.security.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = extractEmail(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken(email);

        jwtService.setRefreshTokenInCookie(response, refreshToken);
        jwtService.sendAccessToken(response, accessToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new EntityNotFoundException("찾을 수 없는 계정입니다.") );

        user.updateRefreshToken(refreshToken);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("nick_name", user.getNickName());
        String responseBody = objectMapper.writeValueAsString(responseMap) + "\n성공적으로 로그인이 완료되었습니다!";

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(responseBody);
        response.getWriter().flush();
        response.getWriter().close();

        log.info( "로그인에 성공합니다. email: {}", email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}", accessToken);
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);
    }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }
}

