package com.toycommerce.gateway.repository;

import com.toycommerce.common.entity.store.StoreBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreBankAccountRepository extends JpaRepository<StoreBankAccount, Long> {
}
