package com.toycommerce.common.exception;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN.getMessage(), ErrorCode.INVALID_TOKEN.getCode());
    }

    public InvalidTokenException(String message) {
        super(message, ErrorCode.INVALID_TOKEN.getCode());
    }

    public InvalidTokenException(Throwable cause) {
        super(ErrorCode.INVALID_TOKEN.getMessage(), ErrorCode.INVALID_TOKEN.getCode(), true, cause);
    }
}
