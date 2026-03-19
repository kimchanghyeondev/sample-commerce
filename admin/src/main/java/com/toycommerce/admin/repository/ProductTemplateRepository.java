package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.product.ProductTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Long> {
    
    Optional<ProductTemplate> findByName(String name);
    
    boolean existsByName(String name);
    
    List<ProductTemplate> findByStatus(EntityStatus status);
    
    Page<ProductTemplate> findByStatus(EntityStatus status, Pageable pageable);
    
    @Query("SELECT pt FROM ProductTemplate pt WHERE pt.status != 'DELETED' ORDER BY pt.createdAt DESC")
    Page<ProductTemplate> findAllActive(Pageable pageable);
    
    @Query("SELECT pt FROM ProductTemplate pt WHERE pt.name LIKE %:keyword% AND pt.status != 'DELETED'")
    Page<ProductTemplate> searchByName(@Param("keyword") String keyword, Pageable pageable);
}

