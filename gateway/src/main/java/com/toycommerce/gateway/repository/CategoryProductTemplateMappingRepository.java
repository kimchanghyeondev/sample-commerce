package com.toycommerce.gateway.repository;

import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryProductTemplateMappingRepository extends JpaRepository<CategoryProductTemplateMapping, Long> {
    
    @Query("SELECT m FROM CategoryProductTemplateMapping m " +
           "JOIN FETCH m.category c " +
           "JOIN FETCH m.productTemplate pt " +
           "WHERE c.id = :categoryId " +
           "ORDER BY m.sortOrder ASC")
    List<CategoryProductTemplateMapping> findByCategoryIdWithDetails(@Param("categoryId") Long categoryId);
    
    @Query("SELECT m FROM CategoryProductTemplateMapping m " +
           "JOIN FETCH m.category c " +
           "JOIN FETCH m.productTemplate pt " +
           "WHERE c.enabled = true " +
           "ORDER BY c.displayOrder ASC, m.sortOrder ASC")
    List<CategoryProductTemplateMapping> findAllEnabledWithDetails();
    
    @Query("SELECT m FROM CategoryProductTemplateMapping m " +
           "JOIN FETCH m.category c " +
           "JOIN FETCH m.productTemplate pt " +
           "WHERE c.parent.id = :parentCategoryId " +
           "ORDER BY c.displayOrder ASC, m.sortOrder ASC")
    List<CategoryProductTemplateMapping> findByParentCategoryIdWithDetails(@Param("parentCategoryId") Long parentCategoryId);
}

