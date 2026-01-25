package com.toycommerce.user.repository;

import com.toycommerce.common.entity.product.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Long> {
    boolean existsByName(String name);
    Optional<ProductTemplate> findByName(String name);
}

