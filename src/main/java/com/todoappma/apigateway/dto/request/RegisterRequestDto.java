package com.todoappma.apigateway.dto.request;

import lombok.Getter;

@Getter
public class RegisterRequestDto {
    private String email;
    private String password;
}
