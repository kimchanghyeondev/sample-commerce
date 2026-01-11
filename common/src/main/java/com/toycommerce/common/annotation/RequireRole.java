package com.toycommerce.common.annotation;

import com.toycommerce.common.entity.user.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 역할(권한)이 필요한 API에 사용하는 어노테이션
 * 이 어노테이션이 적용된 메서드는 지정된 역할을 가진 사용자만 접근할 수 있습니다.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * 필요한 역할 목록
     * @return 역할 배열
     */
    Role[] value();
}

