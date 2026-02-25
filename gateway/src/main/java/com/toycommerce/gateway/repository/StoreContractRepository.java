package com.toycommerce.gateway.repository;

import com.toycommerce.common.entity.store.StoreContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreContractRepository extends JpaRepository<StoreContract, Long> {
}
