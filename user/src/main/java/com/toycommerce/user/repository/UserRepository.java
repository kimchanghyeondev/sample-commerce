package com.toycommerce.user.repository;

import com.toycommerce.common.entity.user.User;
import com.toycommerce.common.exception.BusinessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    /**
     * 사용자 ID로 조회, 없으면 예외 발생
     */
    default User findByIdOrThrow(Long userId) {
        return findById(userId)
                .orElseThrow(() -> new BusinessException(
                        "사용자를 찾을 수 없습니다.",
                        "USER_NOT_FOUND",
                        true
                ));
    }

    /**
     * 사용자명으로 조회, 없으면 예외 발생
     */
    default User findByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new BusinessException(
                        "사용자를 찾을 수 없습니다.",
                        "USER_NOT_FOUND",
                        true
                ));
    }
}

