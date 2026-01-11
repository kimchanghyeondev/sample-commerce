package com.toycommerce.gateway.repository;

import com.toycommerce.common.entity.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProductTemplateId(Long productTemplateId);
    List<ProductOption> findByProductOptionGroupId(Long productOptionGroupId);
    Optional<ProductOption> findByProductTemplateIdAndName(Long productTemplateId, String name);
    boolean existsByProductTemplateIdAndName(Long productTemplateId, String name);
}

