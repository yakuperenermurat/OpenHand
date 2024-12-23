package com.openhand.openhand.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Result<T> {
    private boolean success;
    private String message;
    private T data;

    public Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
