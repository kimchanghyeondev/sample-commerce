package com.toycommerce.user.repository;

import com.toycommerce.common.entity.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProductOptionGroupId(Long productOptionGroupId);
    Optional<ProductOption> findByProductOptionGroupIdAndName(Long productOptionGroupId, String name);
    boolean existsByProductOptionGroupIdAndName(Long productOptionGroupId, String name);
}

