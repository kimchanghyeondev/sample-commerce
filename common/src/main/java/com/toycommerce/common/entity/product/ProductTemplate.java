package com.toycommerce.common.entity.product;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.common.entity.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String name;

    private String description;

    @OneToMany(mappedBy = "productTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryProductTemplateMapping> categories;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;


}
