package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.store.StoreGrade;
import com.toycommerce.common.entity.store.StoreGradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreGradeRepository extends JpaRepository<StoreGrade, Long> {
    
    Optional<StoreGrade> findByGradeType(StoreGradeType gradeType);
    
    boolean existsByGradeType(StoreGradeType gradeType);
}

