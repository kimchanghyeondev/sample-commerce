package com.toycommerce.common.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED.getMessage(), ErrorCode.UNAUTHORIZED.getCode());
    }

    public UnauthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED.getCode());
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }
}
