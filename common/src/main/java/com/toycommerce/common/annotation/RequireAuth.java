package com.toycommerce.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 인증이 필요한 API에 사용하는 어노테이션
 * 이 어노테이션이 적용된 메서드는 JWT 토큰 검증을 수행합니다.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
}

