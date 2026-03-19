package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.store.ContractStatus;
import com.toycommerce.common.entity.store.StoreContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreContractRepository extends JpaRepository<StoreContract, Long> {
    
    List<StoreContract> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    
    @Query("SELECT c FROM StoreContract c WHERE c.store.id = :storeId AND c.status = :status")
    Optional<StoreContract> findByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") ContractStatus status);
    
    @Query("SELECT c FROM StoreContract c WHERE c.store.id = :storeId AND c.status = 'ACTIVE'")
    Optional<StoreContract> findActiveByStoreId(@Param("storeId") Long storeId);
}

