package com.todoappma.apigateway.controller;

import com.todoappma.apigateway.dto.request.LoginRequestDto;
import com.todoappma.apigateway.dto.request.RegisterRequestDto;
import com.todoappma.apigateway.dto.response.LoginResponseDto;
import com.todoappma.apigateway.dto.response.RegisterResponseDto;
import com.todoappma.apigateway.grpc.AuthServiceGrpcClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceGrpcClient authClient;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authClient.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        var result = authClient.login(request);

        Cookie accessCookie = new Cookie("access_token", result.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(900);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh_token", result.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(604800);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }
}
