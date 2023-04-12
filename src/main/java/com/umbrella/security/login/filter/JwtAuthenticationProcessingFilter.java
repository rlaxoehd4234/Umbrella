package com.umbrella.security.login.filter;

import com.umbrella.domain.User.User;
import com.umbrella.domain.User.UserRepository;
import com.umbrella.security.userDetails.UserContext;
import com.umbrella.security.utils.RoleUtil;
import com.umbrella.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final RoleUtil roleUtil;

    private static final String[] NO_CHECK_URL = {"/login", "/signUp"};

    @Value("${jwt.access.header}")
    private String accessHeader;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        for (String noCheck : NO_CHECK_URL) {
            if (request.getRequestURI().equals(noCheck)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String extractRefreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (extractRefreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(request, response, extractRefreshToken);
            filterChain.doFilter(request, response);
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) throws ServletException, IOException {
        Optional<User> findUser = userRepository.findByRefreshToken(refreshToken);
        String extractAccessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (findUser.isPresent()) {
            if (extractAccessToken != null) {
                jwtService.sendAccessToken(response, extractAccessToken);
            }
            else {
                jwtService.sendAccessToken(response, jwtService.createAccessToken(findUser.get().getEmail()));
            }
        }
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   FilterChain filterChain
                                                   ) throws ServletException, IOException {
        jwtService.extractAccessToken(request).filter(
                jwtService::isTokenValid
        ).ifPresent(
                accessToken -> jwtService.extractEmail(accessToken)
                        .ifPresent(
                            email -> userRepository.findByEmail(email).ifPresent(
                                    this::saveAuthentication
                        )
                )
        );

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(User user) {
        UserContext authenticatedUser = new UserContext(user.getEmail(), user.getPassword(), user.getId(), user.getNickName(),
                roleUtil.addAuthoritiesForContext(user));

        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null,
                authoritiesMapper.mapAuthorities(authenticatedUser.getAuthorities()));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
