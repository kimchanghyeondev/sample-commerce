package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.displayOrder")
    List<Category> findRootCategories();
    
    List<Category> findByParentIdOrderByDisplayOrder(Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.displayOrder")
    List<Category> findAllEnabled();
}

