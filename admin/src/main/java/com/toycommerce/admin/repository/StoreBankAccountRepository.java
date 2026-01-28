package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.store.StoreBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreBankAccountRepository extends JpaRepository<StoreBankAccount, Long> {
    
    List<StoreBankAccount> findByStoreId(Long storeId);
    
    @Query("SELECT ba FROM StoreBankAccount ba WHERE ba.store.id = :storeId AND ba.isPrimary = true AND ba.status = 'ACTIVE'")
    Optional<StoreBankAccount> findPrimaryByStoreId(@Param("storeId") Long storeId);
}

