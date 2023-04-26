package com.umbrella.security.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.login.cookie.CookieOAuth2AuthorizationRequestRepository;
import com.umbrella.security.utils.CookieUtil;
import com.umbrella.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.umbrella.security.login.cookie.CookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Log4j2
@Transactional
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final CookieOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Autowired
    private CookieUtil cookieUtil;

    private final String redirectUri = "http://localhost:3000/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Http 응답이 이미 전송되었습니다.");
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
     }

     protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
         Optional<String> redirectUri = cookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                 .map(Cookie::getValue);

         if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
             throw new IllegalArgumentException("Redirect Url 이 일치하지 않습니다!");
         }

         String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
         String email = extractEmailInAuthentication(authentication);

         String accessToken = null;
         try {
             accessToken = setTokenAndSendNickname(email, response);
         } catch (IOException e) {
             // TODO: need Exception Handling
             throw new RuntimeException(e);
         }

         return UriComponentsBuilder.fromUriString(targetUrl).queryParam("accessToken", accessToken)
                 .build().toUriString();
     }

    private String extractEmailInAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }

    private String setTokenAndSendNickname(String email, HttpServletResponse response) throws IOException {
        String refreshToken = jwtService.createRefreshToken(email);
        String accessToken = jwtService.createAccessToken(email);

        jwtService.setRefreshTokenInCookie(response, refreshToken);
        jwtService.sendAccessToken(response, accessToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("찾을 수 없는 계정입니다."));

        user.updateRefreshToken(refreshToken);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("nick_name", user.getNickName());
        String responseBody = new ObjectMapper().writeValueAsString(responseMap) + "\n성공적으로 로그인이 완료되었습니다!";

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(responseBody);

        return accessToken;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUri);

        return authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort();
    }
}
