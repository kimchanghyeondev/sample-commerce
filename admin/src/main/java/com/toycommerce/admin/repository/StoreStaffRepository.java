package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.store.StoreStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreStaffRepository extends JpaRepository<StoreStaff, Long> {
    
    List<StoreStaff> findByStoreId(Long storeId);
    
    @Query("SELECT ss FROM StoreStaff ss WHERE ss.store.id = :storeId AND ss.isPrimary = true AND ss.status = 'ACTIVE'")
    Optional<StoreStaff> findPrimaryByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT ss FROM StoreStaff ss WHERE ss.user.id = :userId AND ss.status = 'ACTIVE'")
    List<StoreStaff> findByUserId(@Param("userId") Long userId);
    
    boolean existsByStoreIdAndUserId(Long storeId, Long userId);
}

