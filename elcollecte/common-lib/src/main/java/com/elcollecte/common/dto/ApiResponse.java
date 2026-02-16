package com.elcollecte.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean       success;
    private String        message;
    private T             data;
    private Object        errors;
    private LocalDateTime timestamp;

    public ApiResponse() { this.timestamp = LocalDateTime.now(); }

    private ApiResponse(boolean success, String message, T data, Object errors) {
        this.success = success; this.message = message;
        this.data = data; this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data, null);
    }
    public static <T> ApiResponse<T> ok(String msg, T data) {
        return new ApiResponse<>(true, msg, data, null);
    }
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(false, msg, null, null);
    }
    public static <T> ApiResponse<T> error(String msg, Object errors) {
        return new ApiResponse<>(false, msg, null, errors);
    }

    public boolean isSuccess()    { return success; }
    public String getMessage()    { return message; }
    public T getData()            { return data; }
    public Object getErrors()     { return errors; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setSuccess(boolean v)   { this.success = v; }
    public void setMessage(String v)    { this.message = v; }
    public void setData(T v)            { this.data = v; }
    public void setErrors(Object v)     { this.errors = v; }
    public void setTimestamp(LocalDateTime v) { this.timestamp = v; }
}