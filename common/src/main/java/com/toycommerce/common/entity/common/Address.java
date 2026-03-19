package com.toycommerce.common.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주소 정보 Embeddable
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "address_detail", length = 200)
    private String addressDetail;

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) {
            sb.append(address);
        }
        if (addressDetail != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(addressDetail);
        }
        return sb.toString();
    }
}

