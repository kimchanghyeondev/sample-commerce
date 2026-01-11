package com.toycommerce.common.entity.product;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.category.Category;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.common.entity.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_template_id", nullable = false, updatable = false)
    private ProductTemplate productTemplate;

    private String sku; // TS-BLK-S

    private int price;

    private int stock;




}
