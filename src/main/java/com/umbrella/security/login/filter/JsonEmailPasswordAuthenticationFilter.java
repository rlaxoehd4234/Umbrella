package com.umbrella.security.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.dto.user.UserRequestLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JsonEmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json";

    public JsonEmailPasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException
    {
        if (!HTTP_METHOD.equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported : " + request.getMethod());
        }

        if (!CONTENT_TYPE.equals(request.getContentType())) {
            throw new AuthenticationServiceException("Authentication method not supported : " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        UserRequestLoginDto userLoginDto = objectMapper.readValue(messageBody, UserRequestLoginDto.class);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(), userLoginDto.getPassword());

        setDetails(request, authToken);

        return getAuthenticationManager().authenticate(authToken);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authToken) {
        authToken.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
