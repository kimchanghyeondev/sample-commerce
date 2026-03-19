package com.toycommerce.gateway.repository;

import com.toycommerce.common.entity.store.StoreOperationHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreOperationHoursRepository extends JpaRepository<StoreOperationHours, Long> {
}
