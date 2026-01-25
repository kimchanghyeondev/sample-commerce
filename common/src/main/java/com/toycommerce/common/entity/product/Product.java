package com.toycommerce.common.entity.product;

import com.toycommerce.common.entity.BaseEntity;
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

    @Column(name = "name", nullable = false)
    private String name; // 예: "갈치조림", "갈치튀김"

    @Column(name = "description")
    private String description;

    @Column(name = "sku", unique = true)
    private String sku; // 예: "GALCHI-JORIM-001"

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EntityStatus status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionGroup> optionGroups;
}
