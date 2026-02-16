package com.elcollecte.common.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public static BusinessException notFound(String msg)   { return new BusinessException(msg, HttpStatus.NOT_FOUND); }
    public static BusinessException forbidden(String msg)  { return new BusinessException(msg, HttpStatus.FORBIDDEN); }
    public static BusinessException badRequest(String msg) { return new BusinessException(msg, HttpStatus.BAD_REQUEST); }
    public static BusinessException conflict(String msg)   { return new BusinessException(msg, HttpStatus.CONFLICT); }
    public static BusinessException unauthorized(String msg){ return new BusinessException(msg, HttpStatus.UNAUTHORIZED); }
}
