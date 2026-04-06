package com.todoappma.apigateway.dto.request;

import lombok.Getter;

@Getter
public class CreateTodoRequestDto {
    private String title;
    private String description;
    private String deadline;
}
