package com.toycommerce.common.exception;

public class ForbiddenException extends BusinessException {

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN.getMessage(), ErrorCode.FORBIDDEN.getCode());
    }

    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN.getCode());
    }
}
