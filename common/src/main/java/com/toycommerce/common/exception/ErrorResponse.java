package com.toycommerce.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 에러 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * 에러 코드 (예: VALIDATION_ERROR, UNAUTHORIZED)
     */
    private String errorCode;
    
    /**
     * 에러 메시지
     */
    private String message;
    
    /**
     * 프론트엔드에 메시지 표시 여부
     * true: 사용자에게 메시지 표시, false: 로그만 기록하고 일반적인 오류 메시지 표시
     */
    private Boolean isShowMessage;
    
    /**
     * 에러 발생 시간
     */
    private LocalDateTime timestamp;
    
    /**
     * 요청 경로
     */
    private String path;
    
    /**
     * 상세 에러 정보 (필드별 검증 오류 등)
     */
    private Map<String, Object> details;
    
    public static ErrorResponse of(String errorCode, String message, boolean isShowMessage, String path) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .isShowMessage(isShowMessage)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
    
    public static ErrorResponse of(String errorCode, String message, boolean isShowMessage, String path, Map<String, Object> details) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .isShowMessage(isShowMessage)
                .timestamp(LocalDateTime.now())
                .path(path)
                .details(details)
                .build();
    }
}

