package com.toycommerce.common.entity.payment;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.product.ProductOption;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, updatable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", nullable = false, updatable = false)
    private ProductOption productOption;

    @Column(name = "quantity", nullable = false, updatable = false)
    private int quantity;

    @Column(name = "origin_price", nullable = false, updatable = false)
    private BigDecimal originPrice;

    @Column(name = "total_discounted_price", nullable = false, updatable = false)
    private BigDecimal totalDiscountedPrice;


}
