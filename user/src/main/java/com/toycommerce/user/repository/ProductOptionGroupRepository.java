package com.toycommerce.user.repository;

import com.toycommerce.common.entity.product.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Long> {
    List<ProductOptionGroup> findByProductId(Long productId);
    Optional<ProductOptionGroup> findByProductIdAndName(Long productId, String name);
    boolean existsByProductIdAndName(Long productId, String name);
}

