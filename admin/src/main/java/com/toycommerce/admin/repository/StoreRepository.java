package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.store.Store;
import com.toycommerce.common.entity.store.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    Optional<Store> findByBusinessNumber(String businessNumber);
    
    boolean existsByBusinessNumber(String businessNumber);
    
    boolean existsByName(String name);
    
    Page<Store> findByStatus(StoreStatus status, Pageable pageable);
    
    @Query("SELECT s FROM Store s WHERE s.status != 'CLOSED'")
    Page<Store> findAllActive(Pageable pageable);
    
    @Query("SELECT s FROM Store s WHERE s.name LIKE %:keyword% OR s.businessNumber LIKE %:keyword%")
    Page<Store> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

