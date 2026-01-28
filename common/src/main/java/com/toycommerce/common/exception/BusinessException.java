package com.toycommerce.common.exception;

import lombok.Getter;

/**
 * 비즈니스 예외 기본 클래스
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final boolean showMessage;
    
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.showMessage = true;
    }
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.showMessage = true;
    }
    
    public BusinessException(String message, String errorCode, boolean showMessage) {
        super(message);
        this.errorCode = errorCode;
        this.showMessage = showMessage;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
        this.showMessage = true;
    }
    
    public BusinessException(String message, String errorCode, boolean showMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.showMessage = showMessage;
    }
}

