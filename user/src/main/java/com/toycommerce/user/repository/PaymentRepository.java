package com.toycommerce.user.repository;

import com.toycommerce.common.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 사용자 ID로 결제 목록 조회 (최신순)
     */
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 사용자 ID와 결제 ID로 조회
     */
    @Query("SELECT p FROM Payment p WHERE p.id = :paymentId AND p.user.id = :userId")
    Payment findByIdAndUserId(@Param("paymentId") Long paymentId, @Param("userId") Long userId);
}

