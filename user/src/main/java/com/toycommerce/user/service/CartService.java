package com.toycommerce.user.service;

import com.toycommerce.common.entity.cart.Cart;
import com.toycommerce.common.entity.cart.CartItem;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.user.User;
import com.toycommerce.user.dto.CartDto;
import com.toycommerce.user.dto.CartItemDto;
import com.toycommerce.user.dto.CartItemRequest;
import com.toycommerce.user.dto.CartItemUpdateRequest;
import com.toycommerce.user.repository.CartItemRepository;
import com.toycommerce.user.repository.CartRepository;
import com.toycommerce.user.repository.ProductOptionRepository;
import com.toycommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 장바구니 조회
     */
    public CartDto getCart(String username) {
        User user = findUserByUsername(username);
        Cart cart = findOrCreateCart(user);
        return CartDto.from(cart);
    }

    /**
     * 장바구니에 상품 추가
     */
    @Transactional
    public CartItemDto addItem(String username, CartItemRequest request) {
        User user = findUserByUsername(username);
        Cart cart = findOrCreateCart(user);
        
        ProductOption productOption = productOptionRepository.findById(request.getProductOptionId())
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다: " + request.getProductOptionId()));

        // 이미 장바구니에 있는 상품인지 확인
        CartItem existingItem = cartItemRepository.findByCartAndProductOption(cart, productOption)
                .orElse(null);

        if (existingItem != null) {
            // 기존 아이템의 수량 증가
            existingItem.updateQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
            log.info("장바구니 아이템 수량 증가: userId={}, productOptionId={}, newQuantity={}", 
                    user.getId(), productOption.getId(), existingItem.getQuantity());
            return CartItemDto.from(existingItem);
        }

        // 새 아이템 추가
        CartItem newItem = CartItem.builder()
                .cart(cart)
                .productOption(productOption)
                .quantity(request.getQuantity())
                .build();

        cart.getCartItems().add(newItem);
        cartRepository.save(cart);
        
        log.info("장바구니에 새 아이템 추가: userId={}, productOptionId={}, quantity={}", 
                user.getId(), productOption.getId(), request.getQuantity());
        
        return CartItemDto.from(newItem);
    }

    /**
     * 장바구니 아이템 수량 수정
     */
    @Transactional
    public CartItemDto updateItemQuantity(String username, Long cartItemId, CartItemUpdateRequest request) {
        User user = findUserByUsername(username);
        Cart cart = findCartByUser(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 아이템을 찾을 수 없습니다: " + cartItemId));

        // 해당 사용자의 장바구니 아이템인지 확인
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("해당 장바구니 아이템에 접근 권한이 없습니다.");
        }

        cartItem.updateQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        log.info("장바구니 아이템 수량 수정: userId={}, cartItemId={}, newQuantity={}", 
                user.getId(), cartItemId, request.getQuantity());

        return CartItemDto.from(cartItem);
    }

    /**
     * 장바구니 아이템 삭제
     */
    @Transactional
    public void removeItem(String username, Long cartItemId) {
        User user = findUserByUsername(username);
        Cart cart = findCartByUser(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 아이템을 찾을 수 없습니다: " + cartItemId));

        // 해당 사용자의 장바구니 아이템인지 확인
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("해당 장바구니 아이템에 접근 권한이 없습니다.");
        }

        cart.getCartItems().remove(cartItem);
        cartRepository.save(cart);

        log.info("장바구니 아이템 삭제: userId={}, cartItemId={}", user.getId(), cartItemId);
    }

    /**
     * 장바구니 비우기
     */
    @Transactional
    public void clearCart(String username) {
        User user = findUserByUsername(username);
        Cart cart = findCartByUser(user);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        log.info("장바구니 비우기: userId={}", user.getId());
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
    }

    private Cart findCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));
    }

    @Transactional
    private Cart findOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }
}

