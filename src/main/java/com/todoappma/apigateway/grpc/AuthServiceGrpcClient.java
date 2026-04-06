package com.todoappma.apigateway.grpc;

import com.todoappma.apigateway.dto.request.LoginRequestDto;
import com.todoappma.apigateway.dto.request.RegisterRequestDto;
import com.todoappma.apigateway.dto.response.LoginResponseDto;
import com.todoappma.apigateway.dto.response.RegisterResponseDto;
import com.todoappma.apigateway.exception.GatewayException;
import com.todoappma.proto.auth.*;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceGrpcClient {

    @GrpcClient("auth-service")
    private AuthServiceGrpc.AuthServiceBlockingStub stub;

    public RegisterResponseDto register(RegisterRequestDto request) {
        try {
            var response = stub.register(RegisterRequestGrpc.newBuilder()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .build());
            return RegisterResponseDto.builder()
                    .userId(response.getUserId())
                    .email(response.getEmail())
                    .build();
        } catch (StatusRuntimeException e) {
            throw mapGrpcException(e);
        }
    }

    public LoginResponseDto login(LoginRequestDto request) {
        try {
            var response = stub.login(LoginRequestGrpc.newBuilder()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .build());
            return LoginResponseDto.builder()
                    .accessToken(response.getAccessToken())
                    .refreshToken(response.getRefreshToken())
                    .userId(response.getUserId())
                    .email(response.getEmail())
                    .build();
        } catch (StatusRuntimeException e) {
            throw mapGrpcException(e);
        }
    }

    private GatewayException mapGrpcException(StatusRuntimeException e) {
        String desc = e.getStatus().getDescription();
        if (desc != null && desc.contains(":")) {
            String[] parts = desc.split(":", 2);
            return new GatewayException(parts[0], parts[1],
                    e.getStatus().getCode().name().equals("UNAUTHENTICATED") ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST);
        }
        return new GatewayException("AUTH_000", "Auth service error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
