package com.toycommerce.user.service;

import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.payment.Payment;
import com.toycommerce.common.entity.payment.PaymentItem;
import com.toycommerce.common.entity.payment.PaymentStatus;
import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.user.User;
import com.toycommerce.common.exception.BusinessException;
import com.toycommerce.user.dto.PaymentRequest;
import com.toycommerce.user.repository.PaymentRepository;
import com.toycommerce.user.repository.ProductOptionRepository;
import com.toycommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ProductOptionRepository productOptionRepository;

    /**
     * 결제 생성
     */
    @Transactional
    public Payment createPayment(Long userId, PaymentRequest request) {
        // 1. 사용자 조회 (공통화된 메서드 사용)
        User user = userRepository.findByIdOrThrow(userId);

        // 2. 상품 옵션 조회 및 검증
        List<Long> productOptionIds = request.getPaymentItems().stream()
                .map(PaymentRequest.PaymentItemRequest::getProductOptionId)
                .toList();

        Map<Long, ProductOption> productOptionMap = productOptionRepository.findAllById(productOptionIds)
                .stream()
                .collect(Collectors.toMap(ProductOption::getId, Function.identity()));

        // 3. 상품 옵션 존재 여부 및 활성화 상태 검증
        validateProductOptions(request, productOptionMap);

        // 4. PaymentItem 생성 (가격 정보 포함)
        Payment payment = Payment.builder()
                .user(user)
                .status(PaymentStatus.PENDING)
                .build();

        List<PaymentItem> paymentItems = createPaymentItems(payment, request, productOptionMap);
        payment.updatePaymentItems(paymentItems);

        // 5. 금액 계산 및 설정
        calculateAndSetAmounts(payment, request);

        // 6. 저장
        return paymentRepository.save(payment);
    }

    /**
     * 상품 옵션 검증
     */
    private void validateProductOptions(PaymentRequest request, Map<Long, ProductOption> productOptionMap) {
        for (PaymentRequest.PaymentItemRequest itemRequest : request.getPaymentItems()) {
            ProductOption productOption = productOptionMap.get(itemRequest.getProductOptionId());
            
            if (productOption == null) {
                throw new BusinessException(
                        "상품 옵션을 찾을 수 없습니다: " + itemRequest.getProductOptionId(),
                        "PRODUCT_OPTION_NOT_FOUND",
                        true
                );
            }

            // 상품 옵션 활성화 상태 확인
            if (productOption.getStatus() != EntityStatus.ACTIVE) {
                throw new BusinessException(
                        "비활성화된 상품 옵션입니다: " + productOption.getName(),
                        "PRODUCT_OPTION_INACTIVE",
                        true
                );
            }

            // 상품 활성화 상태 확인
            Product product = productOption.getProductOptionGroup().getProduct();
            if (product.getStatus() != EntityStatus.ACTIVE) {
                throw new BusinessException(
                        "비활성화된 상품입니다: " + product.getName(),
                        "PRODUCT_INACTIVE",
                        true
                );
            }

            // 재고 확인
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException(
                        String.format("재고가 부족합니다. (요청: %d개, 재고: %d개)", 
                                itemRequest.getQuantity(), product.getStock()),
                        "INSUFFICIENT_STOCK",
                        true
                );
            }
        }
    }

    /**
     * PaymentItem 생성 (가격 정보 포함)
     */
    private List<PaymentItem> createPaymentItems(
            Payment payment, 
            PaymentRequest request, 
            Map<Long, ProductOption> productOptionMap) {
        
        return request.getPaymentItems().stream()
                .map(itemRequest -> {
                    ProductOption productOption = productOptionMap.get(itemRequest.getProductOptionId());
                    Product product = productOption.getProductOptionGroup().getProduct();
                    
                    // 상품 가격 (Integer -> BigDecimal 변환)
                    BigDecimal originPrice = BigDecimal.valueOf(product.getPrice());
                    int quantity = itemRequest.getQuantity();
                    
                    // 할인 금액 계산 (현재는 0, 추후 할인 로직 추가 가능)
                    BigDecimal discountAmount = BigDecimal.ZERO;
                    BigDecimal totalDiscountedPrice = originPrice.multiply(BigDecimal.valueOf(quantity))
                            .subtract(discountAmount);
                    
                    return PaymentItem.builder()
                            .payment(payment)
                            .productOption(productOption)
                            .quantity(quantity)
                            .originPrice(originPrice)
                            .totalDiscountedPrice(totalDiscountedPrice)
                            .build();
                })
                .toList();
    }

    /**
     * 금액 계산 및 설정
     */
    private void calculateAndSetAmounts(Payment payment, PaymentRequest request) {
        // 총 상품 금액 계산
        BigDecimal totalAmount = payment.getPaymentItems().stream()
                .map(item -> item.getOriginPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 총 할인 금액 계산
        BigDecimal totalDiscountAmount = payment.getPaymentItems().stream()
                .map(item -> item.getOriginPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .subtract(item.getTotalDiscountedPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 배송비 (현재는 0, 추후 배송비 계산 로직 추가 가능)
        BigDecimal shippingFee = BigDecimal.ZERO;

        // 금액 설정
        payment.updateAmounts(totalAmount, totalDiscountAmount, shippingFee);

        // 요청된 총 금액과 계산된 금액 검증
        if (request.getTotalPrice() != null) {
            BigDecimal calculatedFinalAmount = payment.getFinalAmount();
            if (request.getTotalPrice().compareTo(calculatedFinalAmount) != 0) {
                log.warn("요청된 총 금액({})과 계산된 금액({})이 일치하지 않습니다.", 
                        request.getTotalPrice(), calculatedFinalAmount);
                // 실제 서비스에서는 이 경우 예외를 던지거나 별도 처리 필요
            }
        }
    }

    /**
     * 결제 조회 (사용자별)
     */
    @Transactional(readOnly = true)
    public Payment getPayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(
                        "결제를 찾을 수 없습니다.",
                        "PAYMENT_NOT_FOUND",
                        true
                ));

        // 본인의 결제인지 확인
        if (!payment.getUser().getId().equals(userId)) {
            throw new BusinessException(
                    "해당 결제에 접근할 수 없습니다.",
                    "PAYMENT_ACCESS_DENIED",
                    true
            );
        }

        return payment;
    }

    /**
     * 사용자의 결제 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Payment> getPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 결제 취소
     */
    @Transactional
    public Payment cancelPayment(Long userId, Long paymentId) {
        Payment payment = getPayment(userId, paymentId);

        // 취소 가능 여부 확인
        if (!payment.isCancellable()) {
            throw new BusinessException(
                    "취소할 수 없는 결제 상태입니다. (현재 상태: " + payment.getStatus() + ")",
                    "PAYMENT_NOT_CANCELLABLE",
                    true
            );
        }

        payment.cancel();
        return paymentRepository.save(payment);
    }

    /**
     * 결제 완료 처리
     */
    @Transactional
    public Payment completePayment(Long userId, Long paymentId, String paymentKey, String transactionId) {
        Payment payment = getPayment(userId, paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(
                    "결제 대기 상태가 아닙니다. (현재 상태: " + payment.getStatus() + ")",
                    "PAYMENT_NOT_PENDING",
                    true
            );
        }

        payment.updatePaymentInfo(paymentKey, transactionId);
        payment.complete();
        return paymentRepository.save(payment);
    }
}
