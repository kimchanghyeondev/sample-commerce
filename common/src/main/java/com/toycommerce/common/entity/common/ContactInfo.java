package com.toycommerce.common.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 연락처 정보 Embeddable
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactInfo {

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;
}

