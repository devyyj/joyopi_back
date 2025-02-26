package com.joyopi.backend.common.security;

import com.joyopi.backend.common.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 인증되지 않은 사용자에 대해 401 Unauthorized 응답을 보냄
        ResponseUtil.writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: You need to log in to access this resource");
    }
}
