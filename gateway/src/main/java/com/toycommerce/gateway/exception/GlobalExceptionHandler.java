package com.toycommerce.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toycommerce.common.exception.ErrorResponse;
import com.toycommerce.common.exception.BusinessException;
import com.toycommerce.common.exception.ForbiddenException;
import com.toycommerce.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        ErrorResponse errorResponse;
        String path = exchange.getRequest().getURI().getPath();

        if (ex instanceof UnauthorizedException e) {
            status = HttpStatus.UNAUTHORIZED;
            errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage(), e.isShowMessage(), path);
            log.warn("Unauthorized: {}", e.getMessage());
        } else if (ex instanceof ForbiddenException e) {
            status = HttpStatus.FORBIDDEN;
            errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage(), e.isShowMessage(), path);
            log.warn("Forbidden: {}", e.getMessage());
        } else if (ex instanceof BusinessException e) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage(), e.isShowMessage(), path);
            log.warn("Business exception: {}", e.getMessage());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = ErrorResponse.of(
                    "INTERNAL_SERVER_ERROR",
                    "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                    false,
                    path
            );
            log.error("Unexpected error occurred", ex);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error writing response", e);
            return exchange.getResponse().setComplete();
        }
    }
}
