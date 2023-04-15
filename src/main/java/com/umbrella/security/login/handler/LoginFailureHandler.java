package com.umbrella.security.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    ObjectMapper objectMapper;

    private static final String CONTENT_TYPE = "application/json; charset=utf8";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", "로그인에 실패하였습니다. 이메일 주소 혹은 비밀번호를 다시 확인해주세요.");

        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
