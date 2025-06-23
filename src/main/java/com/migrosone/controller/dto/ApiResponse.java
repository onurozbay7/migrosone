package com.migrosone.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private String message;
    private T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message) {
        this.message = message;
    }
}
