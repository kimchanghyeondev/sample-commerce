package com.toycommerce.user.repository;

import com.toycommerce.common.entity.cart.Cart;
import com.toycommerce.common.entity.cart.CartItem;
import com.toycommerce.common.entity.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductOption(Cart cart, ProductOption productOption);
    boolean existsByCartAndProductOption(Cart cart, ProductOption productOption);
    void deleteByCartId(Long cartId);
}

