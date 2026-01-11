package com.toycommerce.gateway.repository;

import com.toycommerce.common.entity.product.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Long> {
    List<ProductOptionGroup> findByProductTemplateId(Long productTemplateId);
    Optional<ProductOptionGroup> findByProductTemplateIdAndName(Long productTemplateId, String name);
    boolean existsByProductTemplateIdAndName(Long productTemplateId, String name);
}

