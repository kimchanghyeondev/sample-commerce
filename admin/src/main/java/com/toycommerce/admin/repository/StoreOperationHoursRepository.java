package com.toycommerce.admin.repository;

import com.toycommerce.common.entity.store.StoreOperationHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreOperationHoursRepository extends JpaRepository<StoreOperationHours, Long> {
    
    List<StoreOperationHours> findByStoreIdOrderByDayOfWeek(Long storeId);
    
    Optional<StoreOperationHours> findByStoreIdAndDayOfWeek(Long storeId, DayOfWeek dayOfWeek);
    
    void deleteByStoreId(Long storeId);
}

