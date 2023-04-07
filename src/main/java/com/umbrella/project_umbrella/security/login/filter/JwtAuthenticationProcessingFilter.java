package com.umbrella.project_umbrella.security.login.filter;

import com.umbrella.project_umbrella.domain.User.User;
import com.umbrella.project_umbrella.repository.UserRepository;
import com.umbrella.project_umbrella.security.userDetails.UserContext;
import com.umbrella.project_umbrella.security.utils.CookieUtil;
import com.umbrella.project_umbrella.service.Impl.JwtServiceImpl;
import com.umbrella.project_umbrella.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final String NO_CHECK_URL = "/login";

    @Value("${jwt.access.header}")
    private String accessHeader;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String extractRefreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (extractRefreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(request, response, extractRefreshToken);
            return;
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken).ifPresent(
                user -> jwtService.sendAccessToken(response, jwtService.createAccessToken(
                        user.getEmail())
                )
        );
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

        String role = user.getRole().name();

        List<GrantedAuthority> authorities = new ArrayList<>();
        Assert.isTrue(!role.startsWith("ROLE_"),
                () -> role + " cannot start with ROLE_ (it is automatically added)");
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        UserContext authenticatedUser = new UserContext(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null,
                authoritiesMapper.mapAuthorities(authenticatedUser.getAuthorities()));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
