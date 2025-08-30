package com.veribadge.veribadge.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.veribadge.veribadge.global.status.ErrorStatus;
import com.veribadge.veribadge.global.status.SuccessStatus;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 값을 가지는 필드는 Json 응답에 미포함
@Getter
public class Response<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    private Response(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success(SuccessStatus status, T data) {
        return new Response<>(true, status.getCode(), status.getMessage(), data);
    }

    public static <T> Response<T> fail(ErrorStatus status) {
        return new Response<>(false, status.getCode(), status.getMessage(), null);
    }
}