
package com.eventify.eventify_backend.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final String level;

    public BusinessException(String message, String code, String level) {
        super(message);
        this.code = code;
        this.level = level;
    }

    public BusinessException(String message, String code, String level, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.level = level;
    }
}