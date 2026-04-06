package com.todoappma.apigateway.filter;

import com.todoappma.apigateway.annotation.Authenticated;
import com.todoappma.apigateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AuthFilter implements HandlerInterceptor {

    private final JwtConfig jwtConfig;
    public static final String USER_ID_ATTRIBUTE = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        if (method.getMethodAnnotation(Authenticated.class) == null) {
            return true;
        }

        String token = extractTokenFromCookies(request);
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "AUTH_003:Missing authentication token");
            return false;
        }

        try {
            Claims claims = jwtConfig.validateToken(token);
            request.setAttribute(USER_ID_ATTRIBUTE, claims.getSubject());
            return true;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "AUTH_004:Invalid or expired token");
            return false;
        }
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "access_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
