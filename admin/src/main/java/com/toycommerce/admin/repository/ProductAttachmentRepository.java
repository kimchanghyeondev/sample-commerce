package com.toycommerce.admin.repository;

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
    
    List<ProductAttachment> findByProductIdOrderBySortOrderAsc(Long productId);
    
    List<ProductAttachment> findByProductIdAndAttachmentTypeOrderBySortOrderAsc(Long productId, AttachmentType type);
    
    Optional<ProductAttachment> findByProductIdAndIsPrimaryTrue(Long productId);
    
    @Query("SELECT pa FROM ProductAttachment pa " +
           "JOIN FETCH pa.attachment a " +
           "WHERE pa.product.id = :productId " +
           "AND pa.isPrimary = true " +
           "AND a.status = :status")
    Optional<ProductAttachment> findActivePrimaryByProductIdAndStatus(@Param("productId") Long productId, @Param("status") EntityStatus status);
    
    default Optional<ProductAttachment> findActivePrimaryByProductId(Long productId) {
        return findActivePrimaryByProductIdAndStatus(productId, EntityStatus.ACTIVE);
    }
    
    @Query("SELECT pa FROM ProductAttachment pa " +
           "JOIN FETCH pa.attachment a " +
           "WHERE pa.product.id = :productId " +
           "AND a.status = :status " +
           "ORDER BY pa.sortOrder ASC")
    List<ProductAttachment> findByProductIdAndStatus(@Param("productId") Long productId, @Param("status") EntityStatus status);
    
    default List<ProductAttachment> findActiveByProductId(Long productId) {
        return findByProductIdAndStatus(productId, EntityStatus.ACTIVE);
    }
    
    void deleteByProductIdAndAttachmentId(Long productId, Long attachmentId);
    
    boolean existsByProductIdAndAttachmentId(Long productId, Long attachmentId);
    
    @Query("SELECT COALESCE(MAX(pa.sortOrder), 0) FROM ProductAttachment pa WHERE pa.product.id = :productId")
    Integer findMaxSortOrderByProductId(@Param("productId") Long productId);
}

