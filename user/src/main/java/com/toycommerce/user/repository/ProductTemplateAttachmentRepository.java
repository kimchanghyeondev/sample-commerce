package com.toycommerce.user.repository;

import com.toycommerce.common.entity.attachment.AttachmentType;
import com.toycommerce.common.entity.attachment.ProductTemplateAttachment;
import com.toycommerce.common.entity.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTemplateAttachmentRepository extends JpaRepository<ProductTemplateAttachment, Long> {
    
    @Query("SELECT pa FROM ProductTemplateAttachment pa " +
           "JOIN FETCH pa.attachment a " +
           "WHERE pa.productTemplate.id = :templateId " +
           "AND a.status = :status " +
           "ORDER BY pa.sortOrder ASC")
    List<ProductTemplateAttachment> findByTemplateIdAndStatus(@Param("templateId") Long templateId, @Param("status") EntityStatus status);
    
    default List<ProductTemplateAttachment> findActiveByTemplateId(Long templateId) {
        return findByTemplateIdAndStatus(templateId, EntityStatus.ACTIVE);
    }
    
    @Query("SELECT pa FROM ProductTemplateAttachment pa " +
           "JOIN FETCH pa.attachment a " +
           "WHERE pa.productTemplate.id = :templateId " +
           "AND pa.isPrimary = true " +
           "AND a.status = :status")
    Optional<ProductTemplateAttachment> findPrimaryByTemplateIdAndStatus(@Param("templateId") Long templateId, @Param("status") EntityStatus status);
    
    default Optional<ProductTemplateAttachment> findPrimaryByTemplateId(Long templateId) {
        return findPrimaryByTemplateIdAndStatus(templateId, EntityStatus.ACTIVE);
    }
    
    @Query("SELECT pa FROM ProductTemplateAttachment pa " +
           "JOIN FETCH pa.attachment a " +
           "WHERE pa.productTemplate.id = :templateId " +
           "AND pa.attachmentType = :type " +
           "AND a.status = :status " +
           "ORDER BY pa.sortOrder ASC")
    List<ProductTemplateAttachment> findByTemplateIdAndTypeAndStatus(
            @Param("templateId") Long templateId, 
            @Param("type") AttachmentType type,
            @Param("status") EntityStatus status);
    
    default List<ProductTemplateAttachment> findByTemplateIdAndType(Long templateId, AttachmentType type) {
        return findByTemplateIdAndTypeAndStatus(templateId, type, EntityStatus.ACTIVE);
    }
}

