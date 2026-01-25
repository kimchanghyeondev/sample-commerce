package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.category.Category;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.common.entity.product.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryProductTemplateMappingRepository extends JpaRepository<CategoryProductTemplateMapping, Long> {
    
    List<CategoryProductTemplateMapping> findByCategoryId(Long categoryId);
    
    List<CategoryProductTemplateMapping> findByProductTemplateId(Long productTemplateId);
    
    Optional<CategoryProductTemplateMapping> findByCategoryAndProductTemplate(Category category, ProductTemplate productTemplate);
    
    boolean existsByCategoryAndProductTemplate(Category category, ProductTemplate productTemplate);
    
    void deleteByProductTemplateId(Long productTemplateId);
}

