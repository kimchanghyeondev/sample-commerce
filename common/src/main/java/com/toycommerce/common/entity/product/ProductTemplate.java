package com.toycommerce.common.entity.product;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.common.entity.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_template")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "productTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CategoryProductTemplateMapping> categories = new ArrayList<>();

    @OneToMany(mappedBy = "productTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }
}
