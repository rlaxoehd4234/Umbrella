package com.umbrella.security.login.handler;

import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final String APPLICATION_JSON = "application/json";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = extractEmail(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken(email);

        jwtService.setRefreshTokenInCookie(response, refreshToken);
        jwtService.sendAccessToken(response, accessToken);

        User foundUser = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("찾을 수 없는 계정입니다.")
        );

        foundUser.updateRefreshToken(refreshToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write("성공적으로 로그인이 완료되었습니다!");
        response.getWriter().write(foundUser.getNickName());

        log.info( "로그인에 성공합니다. email: {}", email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}", accessToken);
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);
    }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }
}

