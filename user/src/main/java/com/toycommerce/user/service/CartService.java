package com.toycommerce.user.service;

import com.toycommerce.common.entity.cart.Cart;
import com.toycommerce.common.entity.cart.CartItem;
import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.user.User;
import com.toycommerce.user.dto.*;
import com.toycommerce.user.repository.CartItemRepository;
import com.toycommerce.user.repository.CartRepository;
import com.toycommerce.user.repository.ProductAttachmentRepository;
import com.toycommerce.user.repository.ProductOptionRepository;
import com.toycommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductAttachmentRepository productAttachmentRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 장바구니 조회 (점포별 그룹화)
     */
    public CartDto getCart(String username) {
        User user = findUserByUsername(username);
        Cart cart = findOrCreateCart(user);
        return buildCartDto(cart);
    }

    /**
     * Cart 엔티티를 CartDto로 변환 (점포별 그룹화)
     */
    private CartDto buildCartDto(Cart cart) {
        // 1. 모든 장바구니 아이템을 CartItemDto로 변환
        List<CartItemDto> allItems = cart.getCartItems().stream()
                .map(this::convertToCartItemDto)
                .toList();

        // 2. 점포별로 그룹화
        Map<Long, List<CartItemDto>> groupedByStore = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getStoreId() != null ? item.getStoreId() : 0L,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 3. 점포별 그룹 DTO 생성
        List<StoreCartGroupDto> storeGroups = groupedByStore.entrySet().stream()
                .map(entry -> {
                    Long storeId = entry.getKey();
                    List<CartItemDto> items = entry.getValue();
                    
                    // 첫 번째 아이템에서 점포 정보 추출
                    CartItemDto firstItem = items.get(0);
                    String storeName = firstItem.getStoreName();
                    Integer shippingFee = firstItem.getStoreShippingFee() != null ? firstItem.getStoreShippingFee() : 0;
                    Integer freeShippingThreshold = firstItem.getStoreFreeShippingThreshold();
                    
                    // 소계 및 수량 계산
                    int subtotal = items.stream().mapToInt(CartItemDto::getTotalPrice).sum();
                    int itemCount = items.stream().mapToInt(CartItemDto::getQuantity).sum();
                    
                    // 무료배송 조건 확인
                    int appliedShippingFee = shippingFee;
                    if (freeShippingThreshold != null && subtotal >= freeShippingThreshold) {
                        appliedShippingFee = 0;
                    }
                    
                    return StoreCartGroupDto.builder()
                            .storeId(storeId == 0L ? null : storeId)
                            .storeName(storeName)
                            .shippingFee(shippingFee)
                            .freeShippingThreshold(freeShippingThreshold)
                            .items(items)
                            .itemCount(itemCount)
                            .subtotal(subtotal)
                            .appliedShippingFee(appliedShippingFee)
                            .build();
                })
                .toList();

        // 4. 전체 합계 계산
        int totalQuantity = storeGroups.stream().mapToInt(StoreCartGroupDto::getItemCount).sum();
        int totalProductPrice = storeGroups.stream().mapToInt(StoreCartGroupDto::getSubtotal).sum();
        int totalShippingFee = storeGroups.stream().mapToInt(StoreCartGroupDto::getAppliedShippingFee).sum();

        return CartDto.builder()
                .id(cart.getId())
                .storeGroups(storeGroups)
                .totalQuantity(totalQuantity)
                .totalProductPrice(totalProductPrice)
                .totalShippingFee(totalShippingFee)
                .totalPrice(totalProductPrice + totalShippingFee)
                .storeCount(storeGroups.size())
                .build();
    }

    /**
     * CartItem을 CartItemDto로 변환 (상품 이미지 포함)
     */
    private CartItemDto convertToCartItemDto(CartItem cartItem) {
        Product product = cartItem.getProductOption().getProductOptionGroup().getProduct();
        
        // 상품 대표 이미지 조회
        String primaryImageUrl = productAttachmentRepository.findPrimaryByProductId(product.getId())
                .map(pa -> pa.getAttachment().getFileUrl())
                .orElse(null);
        
        return CartItemDto.from(cartItem, primaryImageUrl);
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
            return convertToCartItemDto(existingItem);
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
        
        return convertToCartItemDto(newItem);
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

        return convertToCartItemDto(cartItem);
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
