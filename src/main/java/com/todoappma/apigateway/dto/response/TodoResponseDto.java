package com.todoappma.apigateway.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoResponseDto {
    private String todoId;
    private String userId;
    private String title;
    private String description;
    private String deadline;
    private boolean done;
    private String createdAt;
    private String updatedAt;
}
