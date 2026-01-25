package com.toycommerce.user.repository;

import com.toycommerce.common.entity.attachment.Attachment;
import com.toycommerce.common.entity.enums.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    
    Optional<Attachment> findByIdAndStatus(Long id, EntityStatus status);
    
    List<Attachment> findByIdInAndStatus(List<Long> ids, EntityStatus status);
}

