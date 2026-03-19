package com.toycommerce.user.repository;

import com.toycommerce.common.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductTemplateId(Long productTemplateId);
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
}

