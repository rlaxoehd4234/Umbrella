package com.umbrella.security.login.filter;

import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.security.utils.RoleUtil;
import com.umbrella.service.JwtService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleUtil roleUtil;

    private static final String[] NO_CHECK_URI_LIST = {"/login", "/signUp"};

    private static final int PASS = 1;
    private static final int REISSUE = 0;

    private static final String REFRESH_TOKEN_ERROR_M = "유효하지 않은 리프레쉬 토큰입니다!";
    private static final String ACCESS_TOKEN_ERROR_M = "유효하지 않은 엑세스 토큰입니다!";

    @Value("${jwt.access.header}")
    private String accessHeader;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (Arrays.stream(NO_CHECK_URI_LIST).anyMatch(uri -> uri.equals(requestURI))) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> extractRefreshToken = jwtService.extractRefreshToken(request);
        String extractAccessToken = jwtService.extractAccessToken(request).orElseThrow(
                () -> new JwtException(ACCESS_TOKEN_ERROR_M)
        );

        if (extractRefreshToken.isPresent()) {
            String email = String.valueOf(jwtService.extractSubject(extractRefreshToken.get()));
            if (jwtService.isTokenValid(extractRefreshToken.get()) == PASS) {
                checkAccessToken(response, extractAccessToken, email);
                checkAndSaveAuthentication(request, response, filterChain,
                        jwtService.extractEmail(extractAccessToken).get());
            } else {
                throw new JwtException(REFRESH_TOKEN_ERROR_M);
            }
        } else {
            throw new JwtException(REFRESH_TOKEN_ERROR_M);
        }

        doFilter(request, response, filterChain);
    }

    private void checkAccessToken(HttpServletResponse response, String accessToken, String email) {
        switch (jwtService.isTokenValid(accessToken)) {
            case PASS:
                jwtService.sendAccessToken(response, accessToken);
                break;
            case REISSUE:
                jwtService.sendAccessToken(response, jwtService.createAccessToken(email));
                break;
            default:
                throw new JwtException(ACCESS_TOKEN_ERROR_M);
        }
    }

    private void checkAndSaveAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain,
                                            String email) throws ServletException, IOException {
        userRepository.findByEmail(email).ifPresent(this::saveAuthentication);
    }

    private void saveAuthentication(User user) {
        UserContext authenticatedUser = new UserContext(
                user.getEmail(),
                user.getPassword(),
                user.getId(),
                user.getNickName(),
                roleUtil.addAuthoritiesForContext(user)
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authoritiesMapper.mapAuthorities(authenticatedUser.getAuthorities())
        );

        SecurityContext securityContext = createSecurityContext(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private SecurityContext createSecurityContext(Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        return securityContext;
    }
}
