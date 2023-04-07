package com.umbrella.project_umbrella.security.login.handler;

import com.umbrella.project_umbrella.security.login.cookie.CookieOAuth2AuthorizationRequestRepository;
import com.umbrella.project_umbrella.security.utils.CookieUtil;
import com.umbrella.project_umbrella.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.umbrella.project_umbrella.security.login.cookie.CookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Log4j2
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private String redirectUri = "http://localhost:3000/oauth2/redirect";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CookieOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Autowired
    private CookieUtil cookieUtil;

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
         String email = extractEmail(authentication);

         String accessToken = jwtService.createAccessToken(email);
         jwtService.createRefreshToken(email);

         return UriComponentsBuilder.fromUriString(targetUrl).queryParam("accessToken", accessToken)
                 .build().toUriString();
     }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUri);

        if (authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
            && authorizedUri.getPort() == clientRedirectUri.getPort()) {
            return true;
        }
        return false;
    }
}
