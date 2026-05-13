package com.english.learn.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResult<T> success(T data) {
        return ApiResult.<T>builder().code(200).message("success").data(data).build();
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return ApiResult.<T>builder().code(200).message(message).data(data).build();
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return ApiResult.<T>builder().code(code).message(message).build();
    }
}
