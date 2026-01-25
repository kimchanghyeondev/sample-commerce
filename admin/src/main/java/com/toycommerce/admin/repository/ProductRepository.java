package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    List<Product> findByProductTemplateId(Long productTemplateId);
    
    Page<Product> findByProductTemplateId(Long productTemplateId, Pageable pageable);
    
    List<Product> findByStatus(EntityStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.status != 'DELETED' ORDER BY p.createdAt DESC")
    Page<Product> findAllActive(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% AND p.status != 'DELETED'")
    Page<Product> searchByName(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.productTemplate.id = :templateId AND p.status != 'DELETED'")
    List<Product> findActiveByTemplateId(@Param("templateId") Long templateId);
}

