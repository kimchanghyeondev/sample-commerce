package com.toycommerce.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    INVALID_CREDENTIALS("AUTH001", "Invalid credentials"),
    UNAUTHORIZED("AUTH002", "Unauthorized"),
    FORBIDDEN("AUTH003", "Forbidden"),
    TOKEN_EXPIRED("AUTH004", "Token has expired"),
    INVALID_TOKEN("AUTH005", "Invalid token"),

    // User
    USER_NOT_FOUND("USER001", "User not found"),
    USER_DISABLED("USER002", "User is disabled"),
    USER_ALREADY_EXISTS("USER003", "User already exists"),

    // Validation
    INVALID_INPUT("VALID001", "Invalid input"),
    MISSING_REQUIRED_FIELD("VALID002", "Missing required field"),

    // System
    INTERNAL_ERROR("SYS001", "Internal server error"),
    SERVICE_UNAVAILABLE("SYS002", "Service unavailable");

    private final String code;
    private final String message;
}
