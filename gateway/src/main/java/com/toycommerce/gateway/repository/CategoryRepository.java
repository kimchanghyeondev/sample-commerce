package com.toycommerce.gateway.repository;

import com.toycommerce.common.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
    List<Category> findByParentIdOrderByDisplayOrderAsc(Long parentId);
    List<Category> findByParentIsNullOrderByDisplayOrderAsc();
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
}

