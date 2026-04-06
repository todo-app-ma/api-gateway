package com.todoappma.apigateway.controller;

import com.todoappma.apigateway.annotation.Authenticated;
import com.todoappma.apigateway.dto.request.CreateTodoRequestDto;
import com.todoappma.apigateway.dto.request.UpdateTodoRequestDto;
import com.todoappma.apigateway.dto.response.TodoResponseDto;
import com.todoappma.apigateway.filter.AuthFilter;
import com.todoappma.apigateway.grpc.TodoServiceGrpcClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoServiceGrpcClient todoClient;

    @Authenticated
    @PostMapping
    public ResponseEntity<TodoResponseDto> createTodo(@RequestBody CreateTodoRequestDto request, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(AuthFilter.USER_ID_ATTRIBUTE);
        return ResponseEntity.ok(todoClient.createTodo(userId, request));
    }

    @Authenticated
    @GetMapping
    public ResponseEntity<List<TodoResponseDto>> getAllTodos(HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(AuthFilter.USER_ID_ATTRIBUTE);
        return ResponseEntity.ok(todoClient.getAllTodos(userId));
    }

    @Authenticated
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponseDto> getTodo(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(AuthFilter.USER_ID_ATTRIBUTE);
        return ResponseEntity.ok(todoClient.getTodo(id, userId));
    }

    @Authenticated
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponseDto> updateTodo(@PathVariable String id, @RequestBody UpdateTodoRequestDto request, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(AuthFilter.USER_ID_ATTRIBUTE);
        return ResponseEntity.ok(todoClient.updateTodo(id, userId, request));
    }

    @Authenticated
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(AuthFilter.USER_ID_ATTRIBUTE);
        todoClient.deleteTodo(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Authenticated
    @PatchMapping("/{id}/done")
    public ResponseEntity<TodoResponseDto> markDone(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute(AuthFilter.USER_ID_ATTRIBUTE);
        return ResponseEntity.ok(todoClient.markTodoDone(id, userId));
    }
}
