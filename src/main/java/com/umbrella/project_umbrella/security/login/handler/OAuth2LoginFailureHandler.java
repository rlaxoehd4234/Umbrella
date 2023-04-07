package com.umbrella.project_umbrella.security.login.handler;

import com.umbrella.project_umbrella.security.login.cookie.CookieOAuth2AuthorizationRequestRepository;
import com.umbrella.project_umbrella.security.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.umbrella.project_umbrella.security.login.cookie.CookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private CookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Autowired
    private CookieUtil cookieUtil;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = cookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                                        .map(Cookie::getValue)
                                        .orElse("/");

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                        .queryParam("error", exception.getLocalizedMessage())
                        .build().toUriString();

        authorizationRequestRepository .removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
