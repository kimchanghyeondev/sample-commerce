package com.toycommerce.gateway.repository;

import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryProductTemplateMappingRepository extends JpaRepository<CategoryProductTemplateMapping, Long> {
}
