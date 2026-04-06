package com.todoappma.apigateway.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponseDto {
    private String userId;
    private String email;
}
