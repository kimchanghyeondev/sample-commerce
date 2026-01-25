package com.toycommerce.user.repository;

import com.toycommerce.common.entity.attachment.AttachmentType;
import com.toycommerce.common.entity.attachment.ProductAttachment;
import com.toycommerce.common.entity.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttachmentRepository extends JpaRepository<ProductAttachment, Long> {
    
    @Query("SELECT pa FROM ProductAttachment pa " +
           "JOIN FETCH pa.attachment a " +
           "WHERE pa.product.id = :productId " +
           "AND a.status = :status " +
           "ORDER BY pa.sortOrder ASC")
    List<ProductAttachment> findByProductIdAndStatus(@Param("productId") Long productId, @Param("status") EntityStatus status);
    
    default List<ProductAttachment> findActiveByProductId(Long productId) {
        return findByProductIdAndStatus(productId, EntityStatus.ACTIVE);
    }
    
    @Query("SELECT pa FROM ProductAttachment pa " +
           "JOIN FETCH pa.attachment a " +
           "WHERE pa.product.id = :productId " +
           "AND pa.isPrimary = true " +
           "AND a.status = :status")
    Optional<ProductAttachment> findPrimaryByProductIdAndStatus(@Param("productId") Long productId, @Param("status") EntityStatus status);
    
    default Optional<ProductAttachment> findPrimaryByProductId(Long productId) {
        return findPrimaryByProductIdAndStatus(productId, EntityStatus.ACTIVE);
    }
    
    @Query("SELECT pa FROM ProductAttachment pa " +
           "JOIN FETCH pa.attachment a " +
           "WHERE pa.product.id = :productId " +
           "AND pa.attachmentType = :type " +
           "AND a.status = :status " +
           "ORDER BY pa.sortOrder ASC")
    List<ProductAttachment> findByProductIdAndTypeAndStatus(
            @Param("productId") Long productId, 
            @Param("type") AttachmentType type,
            @Param("status") EntityStatus status);
    
    default List<ProductAttachment> findByProductIdAndType(Long productId, AttachmentType type) {
        return findByProductIdAndTypeAndStatus(productId, type, EntityStatus.ACTIVE);
    }
}

