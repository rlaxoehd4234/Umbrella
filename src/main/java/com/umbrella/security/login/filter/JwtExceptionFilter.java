package com.umbrella.security.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.exception.ExceptionDto;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            handleJwtException(response, e.getMessage());
        }
    }

    private void handleJwtException(HttpServletResponse response, String message) throws IOException {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.FORBIDDEN.value(), message);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(exceptionDto));
    }
}
