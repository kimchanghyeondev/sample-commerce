package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.product.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Long> {
    
    List<ProductOptionGroup> findByProductId(Long productId);
    
    Optional<ProductOptionGroup> findByProductIdAndName(Long productId, String name);
    
    boolean existsByProductIdAndName(Long productId, String name);
    
    @Query("SELECT pog FROM ProductOptionGroup pog WHERE pog.product.id = :productId AND pog.status != 'DELETED'")
    List<ProductOptionGroup> findActiveByProductId(@Param("productId") Long productId);
}

