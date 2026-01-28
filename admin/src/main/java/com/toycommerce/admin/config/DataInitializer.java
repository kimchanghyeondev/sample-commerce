package com.toycommerce.admin.config;

import com.toycommerce.admin.repository.*;
import com.toycommerce.common.entity.common.Address;
import com.toycommerce.common.entity.common.ContactInfo;
import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.store.*;
import com.toycommerce.common.entity.user.Role;
import com.toycommerce.common.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 개발/테스트 환경용 초기 데이터 설정
 */
@Component
@Profile({"dev", "local", "default"})
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final StoreGradeRepository storeGradeRepository;
    private final StoreRepository storeRepository;
    private final StoreContractRepository storeContractRepository;
    private final StoreBankAccountRepository storeBankAccountRepository;
    private final StoreStaffRepository storeStaffRepository;
    private final StoreOperationHoursRepository storeOperationHoursRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initStoreGrades();
        initSampleStore();
    }

    /**
     * 점포 등급 초기화
     */
    private void initStoreGrades() {
        if (storeGradeRepository.count() > 0) {
            log.info("Store grades already initialized");
            return;
        }

        log.info("Initializing store grades...");

        // BRONZE 등급
        storeGradeRepository.save(StoreGrade.builder()
                .gradeType(StoreGradeType.BRONZE)
                .commissionRate(new BigDecimal("15.00"))
                .description("신규 입점 업체를 위한 기본 등급입니다.")
                .sortOrder(1)
                .status(EntityStatus.ACTIVE)
                .build());

        // SILVER 등급
        storeGradeRepository.save(StoreGrade.builder()
                .gradeType(StoreGradeType.SILVER)
                .commissionRate(new BigDecimal("12.00"))
                .description("월 매출 500만원 이상 달성 업체를 위한 등급입니다.")
                .sortOrder(2)
                .status(EntityStatus.ACTIVE)
                .build());

        // GOLD 등급
        storeGradeRepository.save(StoreGrade.builder()
                .gradeType(StoreGradeType.GOLD)
                .commissionRate(new BigDecimal("10.00"))
                .description("월 매출 1000만원 이상 달성 업체를 위한 등급입니다.")
                .sortOrder(3)
                .status(EntityStatus.ACTIVE)
                .build());

        // PLATINUM 등급
        storeGradeRepository.save(StoreGrade.builder()
                .gradeType(StoreGradeType.PLATINUM)
                .commissionRate(new BigDecimal("8.00"))
                .description("월 매출 3000만원 이상 달성 업체를 위한 프리미엄 등급입니다.")
                .sortOrder(4)
                .status(EntityStatus.ACTIVE)
                .build());

        // DIAMOND 등급
        storeGradeRepository.save(StoreGrade.builder()
                .gradeType(StoreGradeType.DIAMOND)
                .commissionRate(new BigDecimal("5.00"))
                .description("최상위 파트너 업체를 위한 VIP 등급입니다.")
                .sortOrder(5)
                .status(EntityStatus.ACTIVE)
                .build());

        log.info("Store grades initialized successfully");
    }

    /**
     * 샘플 업체 초기화 - 창현산지수산
     */
    private void initSampleStore() {
        if (storeRepository.existsByName("창현산지수산")) {
            log.info("Sample store '창현산지수산' already exists");
            return;
        }

        log.info("Initializing sample store '창현산지수산'...");

        // 1. 판매자 계정 생성
        User seller = userRepository.findByUsername("seller_changhyun")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("seller_changhyun")
                        .password(passwordEncoder.encode("seller1234"))
                        .email("changhyun@example.com")
                        .role(Role.SELLER)
                        .build()));

        // 2. 등급 조회 (SILVER)
        StoreGrade silverGrade = storeGradeRepository.findByGradeType(StoreGradeType.SILVER)
                .orElseThrow(() -> new RuntimeException("SILVER grade not found"));

        // 3. 업체 생성
        Store store = Store.builder()
                .name("창현산지수산")
                .businessNumber("123-45-67890")
                .representativeName("김창현")
                .description("신선한 제주 갈치와 해산물을 직접 산지에서 배송해드립니다. 30년 전통의 수산 전문 업체입니다.")
                .contactInfo(ContactInfo.builder()
                        .phone("064-123-4567")
                        .email("changhyun.fish@example.com")
                        .build())
                .businessAddress(Address.builder()
                        .zipCode("63000")
                        .address("제주특별자치도 제주시 해안로 123")
                        .addressDetail("수산시장 2동 15호")
                        .build())
                .shippingAddress(Address.builder()
                        .zipCode("63000")
                        .address("제주특별자치도 제주시 해안로 125")
                        .addressDetail("창현산지수산 물류센터")
                        .build())
                .defaultShippingFee(3000)
                .freeShippingThreshold(50000)
                .status(StoreStatus.APPROVED)
                .storeGrade(silverGrade)
                .build();
        store = storeRepository.save(store);

        // 4. 계약 생성
        StoreContract contract = StoreContract.builder()
                .store(store)
                .storeGrade(silverGrade)
                .commissionRate(silverGrade.getCommissionRate())
                .contractStartDate(LocalDate.now().minusMonths(6))
                .contractEndDate(LocalDate.now().plusMonths(6))
                .settlementCycle(SettlementCycle.MONTHLY)
                .settlementDay(15)
                .status(ContractStatus.ACTIVE)
                .memo("2025년 상반기 계약")
                .build();
        contract = storeContractRepository.save(contract);

        // 5. 업체에 활성 계약 설정
        store.updateActiveContract(contract);
        storeRepository.save(store);

        // 6. 정산 계좌 생성
        storeBankAccountRepository.save(StoreBankAccount.builder()
                .store(store)
                .bankCode("004")
                .bankName("국민은행")
                .accountNumber("123-456-789012")
                .accountHolder("김창현")
                .isPrimary(true)
                .isVerified(true)
                .status(EntityStatus.ACTIVE)
                .build());

        // 7. 직원(대표) 등록
        storeStaffRepository.save(StoreStaff.builder()
                .store(store)
                .user(seller)
                .staffRole(StoreStaffRole.OWNER)
                .isPrimary(true)
                .status(EntityStatus.ACTIVE)
                .build());

        // 8. 운영 시간 설정
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
            storeOperationHoursRepository.save(StoreOperationHours.builder()
                    .store(store)
                    .dayOfWeek(day)
                    .isOpen(!isWeekend || day == DayOfWeek.SATURDAY) // 일요일 휴무
                    .openTime(isWeekend ? LocalTime.of(10, 0) : LocalTime.of(6, 0))
                    .closeTime(isWeekend ? LocalTime.of(14, 0) : LocalTime.of(18, 0))
                    .breakStart(LocalTime.of(12, 0))
                    .breakEnd(LocalTime.of(13, 0))
                    .build());
        }

        log.info("Sample store '창현산지수산' initialized successfully with ID: {}", store.getId());
    }
}

