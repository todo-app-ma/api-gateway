package com.todoappma.apigateway.grpc;

import com.todoappma.apigateway.dto.request.CreateTodoRequestDto;
import com.todoappma.apigateway.dto.request.UpdateTodoRequestDto;
import com.todoappma.apigateway.dto.response.TodoResponseDto;
import com.todoappma.apigateway.exception.GatewayException;
import com.todoappma.proto.todo.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TodoServiceGrpcClient {

    @GrpcClient("todo-service")
    private TodoServiceGrpc.TodoServiceBlockingStub stub;

    public TodoResponseDto createTodo(String userId, CreateTodoRequestDto request) {
        try {
            var response = stub.createTodo(CreateTodoRequestGrpc.newBuilder()
                    .setUserId(userId)
                    .setTitle(request.getTitle())
                    .setDescription(request.getDescription() != null ? request.getDescription() : "")
                    .setDeadline(request.getDeadline())
                    .build());
            return toDto(response);
        } catch (StatusRuntimeException e) {
            throw mapGrpcException(e);
        }
    }

    public TodoResponseDto getTodo(String todoId, String userId) {
        try {
            var response = stub.getTodo(GetTodoRequestGrpc.newBuilder()
                    .setTodoId(todoId).setUserId(userId).build());
            return toDto(response);
        } catch (StatusRuntimeException e) {
            throw mapGrpcException(e);
        }
    }

    public List<TodoResponseDto> getAllTodos(String userId) {
        try {
            var response = stub.getAllTodos(GetAllTodosRequestGrpc.newBuilder().setUserId(userId).build());
            return response.getTodosList().stream().map(this::toDto).toList();
        } catch (StatusRuntimeException e) {
            throw mapGrpcException(e);
        }
    }

    public TodoResponseDto updateTodo(String todoId, String userId, UpdateTodoRequestDto request) {
        try {
            var response = stub.updateTodo(UpdateTodoRequestGrpc.newBuilder()
                    .setTodoId(todoId)
                    .setUserId(userId)
                    .setTitle(request.getTitle())
                    .setDescription(request.getDescription() != null ? request.getDescription() : "")
                    .setDeadline(request.getDeadline())
                    .build());
            return toDto(response);
        } catch (StatusRuntimeException e) {
            throw mapGrpcException(e);
        }
    }

    public void deleteTodo(String todoId, String userId) {
        try {
            stub.deleteTodo(DeleteTodoRequestGrpc.newBuilder().setTodoId(todoId).setUserId(userId).build());
        } catch (StatusRuntimeException e) {
            throw mapGrpcException(e);
        }
    }

    public TodoResponseDto markTodoDone(String todoId, String userId) {
        try {
            var response = stub.markTodoDone(MarkTodoDoneRequestGrpc.newBuilder()
                    .setTodoId(todoId).setUserId(userId).build());
            return toDto(response);
        } catch (StatusRuntimeException e) {
            throw mapGrpcException(e);
        }
    }

    private TodoResponseDto toDto(TodoResponseGrpc grpc) {
        return TodoResponseDto.builder()
                .todoId(grpc.getTodoId())
                .userId(grpc.getUserId())
                .title(grpc.getTitle())
                .description(grpc.getDescription())
                .deadline(grpc.getDeadline())
                .done(grpc.getDone())
                .createdAt(grpc.getCreatedAt())
                .updatedAt(grpc.getUpdatedAt())
                .build();
    }

    private GatewayException mapGrpcException(StatusRuntimeException e) {
        String desc = e.getStatus().getDescription();
        if (desc != null && desc.contains(":")) {
            String[] parts = desc.split(":", 2);
            HttpStatus status = switch (e.getStatus().getCode()) {
                case NOT_FOUND -> HttpStatus.NOT_FOUND;
                case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
                default -> HttpStatus.BAD_REQUEST;
            };
            return new GatewayException(parts[0], parts[1], status);
        }
        return new GatewayException("TODO_000", "Todo service error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
