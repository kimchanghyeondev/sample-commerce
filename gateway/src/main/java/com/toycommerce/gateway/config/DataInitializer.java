package com.toycommerce.gateway.config;

import com.toycommerce.common.entity.category.Category;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.common.entity.common.Address;
import com.toycommerce.common.entity.common.ContactInfo;
import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.product.ProductOptionGroup;
import com.toycommerce.common.entity.product.ProductTemplate;
import com.toycommerce.common.entity.store.*;
import com.toycommerce.common.entity.user.Role;
import com.toycommerce.common.entity.user.User;
import com.toycommerce.gateway.repository.*;
import com.toycommerce.gateway.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;
    private final StoreGradeRepository storeGradeRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final StoreContractRepository storeContractRepository;
    private final StoreBankAccountRepository storeBankAccountRepository;
    private final StoreStaffRepository storeStaffRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductRepository productRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final CategoryProductTemplateMappingRepository categoryProductTemplateMappingRepository;
    private final StoreOperationHoursRepository storeOperationHoursRepository;
    private final ProductOptionRepository productOptionRepository;

    @PostConstruct
    @Transactional
    public void init() {
        initUsers();
        initStoreGrades();
        initCategories();
        initStoresAndProducts();
        log.info("=== 초기 데이터 생성 완료 ===");
    }

    // ==================== 사용자 ====================

    private void initUsers() {
        try {
            userService.createOrUpdateUser("admin", "admin", Role.ADMIN);
            log.info("Admin user initialized: admin/admin");

            userService.createOrUpdateUser("user", "user", Role.USER);
            log.info("User initialized: user/user");

            userService.createOrUpdateUser("seller1", "seller1", Role.SELLER);
            log.info("Seller initialized: seller1/seller1");

            userService.createOrUpdateUser("seller2", "seller2", Role.SELLER);
            log.info("Seller initialized: seller2/seller2");
        } catch (Exception e) {
            log.error("Failed to initialize users", e);
        }
    }

    // ==================== 입점업체 등급 ====================

    private void initStoreGrades() {
        try {
            if (storeGradeRepository.count() > 0) {
                log.info("StoreGrade already exists - skip");
                return;
            }

            storeGradeRepository.save(StoreGrade.builder()
                    .gradeType(StoreGradeType.BRONZE).commissionRate(new BigDecimal("10.00"))
                    .description("신규 입점 업체").sortOrder(1).build());

            storeGradeRepository.save(StoreGrade.builder()
                    .gradeType(StoreGradeType.SILVER).commissionRate(new BigDecimal("8.00"))
                    .description("월 매출 500만원 이상").sortOrder(2).build());

            storeGradeRepository.save(StoreGrade.builder()
                    .gradeType(StoreGradeType.GOLD).commissionRate(new BigDecimal("6.00"))
                    .description("월 매출 2000만원 이상").sortOrder(3).build());

            storeGradeRepository.save(StoreGrade.builder()
                    .gradeType(StoreGradeType.PLATINUM).commissionRate(new BigDecimal("4.00"))
                    .description("월 매출 5000만원 이상").sortOrder(4).build());

            storeGradeRepository.save(StoreGrade.builder()
                    .gradeType(StoreGradeType.DIAMOND).commissionRate(new BigDecimal("3.00"))
                    .description("월 매출 1억원 이상").sortOrder(5).build());

            log.info("StoreGrade initialized");
        } catch (Exception e) {
            log.error("Failed to initialize store grades", e);
        }
    }

    // ==================== 카테고리 ====================

    private void initCategories() {
        try {
            if (categoryRepository.count() > 0) {
                log.info("Category already exists - skip");
                return;
            }

            // 농산물
            Category farm = categoryRepository.save(Category.builder()
                    .name("농산물").description("신선한 국내산 농산물").displayOrder(1).enabled(true).build());

            categoryRepository.save(Category.builder()
                    .name("채소").description("제철 채소").displayOrder(1).enabled(true).parent(farm).build());
            categoryRepository.save(Category.builder()
                    .name("과일").description("제철 과일").displayOrder(2).enabled(true).parent(farm).build());
            categoryRepository.save(Category.builder()
                    .name("쌀/잡곡").description("국내산 쌀과 잡곡").displayOrder(3).enabled(true).parent(farm).build());

            // 수산물
            Category sea = categoryRepository.save(Category.builder()
                    .name("수산물").description("신선한 국내산 수산물").displayOrder(2).enabled(true).build());

            categoryRepository.save(Category.builder()
                    .name("생선").description("자연산/양식 생선").displayOrder(1).enabled(true).parent(sea).build());
            categoryRepository.save(Category.builder()
                    .name("해산물").description("새우, 게, 조개 등").displayOrder(2).enabled(true).parent(sea).build());
            categoryRepository.save(Category.builder()
                    .name("건어물").description("건오징어, 건새우 등").displayOrder(3).enabled(true).parent(sea).build());

            // 축산물
            Category meat = categoryRepository.save(Category.builder()
                    .name("축산물").description("국내산 축산물").displayOrder(3).enabled(true).build());

            categoryRepository.save(Category.builder()
                    .name("소고기").description("한우/국내산 소고기").displayOrder(1).enabled(true).parent(meat).build());
            categoryRepository.save(Category.builder()
                    .name("돼지고기").description("국내산 돼지고기").displayOrder(2).enabled(true).parent(meat).build());
            categoryRepository.save(Category.builder()
                    .name("닭/오리").description("국내산 닭고기, 오리고기").displayOrder(3).enabled(true).parent(meat).build());

            log.info("Category initialized");
        } catch (Exception e) {
            log.error("Failed to initialize categories", e);
        }
    }

    // ==================== 입점업체 + 상품 ====================

    private void initStoresAndProducts() {
        try {
            if (storeRepository.count() > 0) {
                log.info("Store already exists - skip");
                return;
            }

            StoreGrade bronze = storeGradeRepository.findByGradeType(StoreGradeType.BRONZE).orElseThrow();
            StoreGrade silver = storeGradeRepository.findByGradeType(StoreGradeType.SILVER).orElseThrow();

            User seller1 = userService.findByUsername("seller1").orElseThrow();
            User seller2 = userService.findByUsername("seller2").orElseThrow();

            // ===== 제주싱싱농장 =====
            Store jejuFarm = createStore("제주싱싱농장", "123-45-67890", "김제주",
                    "제주도 직송 신선 농산물 전문점", silver,
                    "064-123-4567", "jejufarm@example.com",
                    "63122", "제주특별자치도 제주시 농산로 100", "제주농산물센터 2층",
                    3000, 30000);
            createStoreContract(jejuFarm, silver, new BigDecimal("8.00"));
            createStoreBankAccount(jejuFarm, "농협", "301-1234-5678-01", "김제주");
            createStoreStaff(jejuFarm, seller1, StoreStaffRole.OWNER);
            createStoreOperationHours(jejuFarm);

            // 제주 감귤
            Category fruit = categoryRepository.findByName("과일").orElseThrow();
            ProductTemplate tangerineTemplate = createProductTemplate("제주 감귤", "제주도 노지 감귤, 새콤달콤한 맛");
            createCategoryMapping(fruit, tangerineTemplate, 1);

            Product tangerine = createProduct("제주 노지 감귤 3kg", "감귤의 본고장 제주에서 직송",
                    "JEJU-TANG-001", 15900, 200, tangerineTemplate, jejuFarm);
            createProductOptions(tangerine, "중량", "3kg", "5kg", "10kg");

            Product hallabong = createProduct("제주 한라봉 2kg", "달콤한 제주 한라봉 선물세트",
                    "JEJU-HALLA-001", 29800, 100, tangerineTemplate, jejuFarm);
            createProductOptions(hallabong, "중량", "2kg", "5kg");

            // 제주 당근
            Category veggie = categoryRepository.findByName("채소").orElseThrow();
            ProductTemplate carrotTemplate = createProductTemplate("제주 당근", "제주 구좌 흙당근");
            createCategoryMapping(veggie, carrotTemplate, 1);

            Product carrot = createProduct("제주 구좌 흙당근 3kg", "비타민 가득 제주 당근",
                    "JEJU-CARROT-001", 12500, 150, carrotTemplate, jejuFarm);
            createProductOptions(carrot, "중량", "3kg", "5kg", "10kg");

            // 제주 감자
            ProductTemplate potatoTemplate = createProductTemplate("제주 감자", "제주 흙감자");
            createCategoryMapping(veggie, potatoTemplate, 2);

            Product potato = createProduct("제주 햇감자 5kg", "포슬포슬 제주 햇감자",
                    "JEJU-POTATO-001", 14900, 180, potatoTemplate, jejuFarm);
            createProductOptions(potato, "중량", "5kg", "10kg");

            // ===== 부산바다수산 =====
            Store busanFish = createStore("부산바다수산", "234-56-78901", "박부산",
                    "부산 자갈치시장 직송 수산물 전문점", bronze,
                    "051-234-5678", "busanfish@example.com",
                    "48994", "부산광역시 중구 자갈치해안로 52", "자갈치시장 1동 201호",
                    5000, 50000);
            createStoreContract(busanFish, bronze, new BigDecimal("10.00"));
            createStoreBankAccount(busanFish, "수협", "070-1234-5678-02", "박부산");
            createStoreStaff(busanFish, seller2, StoreStaffRole.OWNER);
            createStoreOperationHours(busanFish);

            // 고등어
            Category fish = categoryRepository.findByName("생선").orElseThrow();
            ProductTemplate mackerelTemplate = createProductTemplate("부산 고등어", "부산 자갈치 싱싱 고등어");
            createCategoryMapping(fish, mackerelTemplate, 1);

            Product mackerel = createProduct("자갈치 싱싱 고등어 2마리", "당일 경매 자연산 고등어",
                    "BS-MACK-001", 12000, 300, mackerelTemplate, busanFish);
            createProductOptions(mackerel, "마리수", "2마리", "4마리", "6마리");

            // 갈치
            ProductTemplate cutlassTemplate = createProductTemplate("제주 은갈치", "제주 근해 자연산 은갈치");
            createCategoryMapping(fish, cutlassTemplate, 2);

            Product cutlass = createProduct("제주 은갈치 대 2마리", "은빛 자연산 갈치",
                    "BS-GAL-001", 28000, 120, cutlassTemplate, busanFish);
            createProductOptions(cutlass, "크기", "중(2마리)", "대(2마리)", "특대(2마리)");

            // 새우
            Category seafood = categoryRepository.findByName("해산물").orElseThrow();
            ProductTemplate shrimpTemplate = createProductTemplate("통영 새우", "통영 자연산 대하");
            createCategoryMapping(seafood, shrimpTemplate, 1);

            Product shrimp = createProduct("통영 자연산 대하 1kg", "살아있는 통영 대하",
                    "BS-SHRIMP-001", 35000, 80, shrimpTemplate, busanFish);
            createProductOptions(shrimp, "중량", "500g", "1kg", "2kg");

            // 건오징어
            Category dried = categoryRepository.findByName("건어물").orElseThrow();
            ProductTemplate driedSquidTemplate = createProductTemplate("울릉도 건오징어", "울릉도산 건오징어");
            createCategoryMapping(dried, driedSquidTemplate, 1);

            Product driedSquid = createProduct("울릉도 건오징어 10마리", "바람에 말린 울릉도 오징어",
                    "BS-DSQUID-001", 42000, 60, driedSquidTemplate, busanFish);
            createProductOptions(driedSquid, "마리수", "5마리", "10마리", "20마리");

            // ===== 상주한우목장 =====
            Store hanuFarm = createStore("상주한우목장", "345-67-89012", "이상주",
                    "경북 상주 한우 직영 목장", silver,
                    "054-345-6789", "hanufarm@example.com",
                    "37164", "경상북도 상주시 한우로 200", "상주축산센터 3동",
                    5000, 70000);
            createStoreContract(hanuFarm, silver, new BigDecimal("8.00"));
            createStoreBankAccount(hanuFarm, "농협", "302-5678-9012-03", "이상주");
            createStoreStaff(hanuFarm, seller1, StoreStaffRole.MANAGER);
            createStoreOperationHours(hanuFarm);

            // 한우
            Category beef = categoryRepository.findByName("소고기").orElseThrow();
            ProductTemplate hanuTemplate = createProductTemplate("상주 한우", "1++ 등급 상주 한우");
            createCategoryMapping(beef, hanuTemplate, 1);

            Product hanuSet = createProduct("한우 1++ 등심 세트 600g", "최상급 상주 한우 등심",
                    "SJ-HANU-001", 89000, 50, hanuTemplate, hanuFarm);
            createProductOptions(hanuSet, "중량", "300g", "600g", "1kg");

            Product hanuGalbi = createProduct("한우 갈비살 500g", "부드러운 한우 갈비살",
                    "SJ-HANU-002", 59000, 70, hanuTemplate, hanuFarm);
            createProductOptions(hanuGalbi, "중량", "500g", "1kg");

            // 돼지고기
            Category pork = categoryRepository.findByName("돼지고기").orElseThrow();
            ProductTemplate porkTemplate = createProductTemplate("제주 흑돼지", "제주 방목 흑돼지");
            createCategoryMapping(pork, porkTemplate, 1);

            Product blackPork = createProduct("제주 흑돼지 삼겹살 500g", "제주 방목 흑돼지 삼겹살",
                    "SJ-PORK-001", 32000, 90, porkTemplate, hanuFarm);
            createProductOptions(blackPork, "중량", "500g", "1kg");

            // 쌀
            Category rice = categoryRepository.findByName("쌀/잡곡").orElseThrow();
            ProductTemplate riceTemplate = createProductTemplate("상주 쌀", "경북 상주 일품미");
            createCategoryMapping(rice, riceTemplate, 1);

            Product sangJuRice = createProduct("상주 일품미 10kg", "윤기나는 상주 쌀",
                    "SJ-RICE-001", 38000, 200, riceTemplate, hanuFarm);
            createProductOptions(sangJuRice, "중량", "5kg", "10kg", "20kg");

            log.info("Store & Product initialized");
        } catch (Exception e) {
            log.error("Failed to initialize stores and products", e);
        }
    }

    // ==================== Helper Methods ====================

    private Store createStore(String name, String bizNumber, String representative,
                              String description, StoreGrade grade,
                              String phone, String email,
                              String zipCode, String address, String addressDetail,
                              int shippingFee, int freeShippingThreshold) {
        ContactInfo contactInfo = ContactInfo.builder()
                .phone(phone).email(email).build();

        Address bizAddress = Address.builder()
                .zipCode(zipCode).address(address).addressDetail(addressDetail).build();

        return storeRepository.save(Store.builder()
                .name(name)
                .businessNumber(bizNumber)
                .representativeName(representative)
                .description(description)
                .storeGrade(grade)
                .contactInfo(contactInfo)
                .businessAddress(bizAddress)
                .shippingAddress(bizAddress)
                .defaultShippingFee(shippingFee)
                .freeShippingThreshold(freeShippingThreshold)
                .status(StoreStatus.APPROVED)
                .build());
    }

    private void createStoreContract(Store store, StoreGrade grade, BigDecimal commissionRate) {
        storeContractRepository.save(StoreContract.builder()
                .store(store)
                .storeGrade(grade)
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusYears(1))
                .commissionRate(commissionRate)
                .settlementCycle(SettlementCycle.MONTHLY)
                .settlementDay(15)
                .status(ContractStatus.ACTIVE)
                .build());
    }

    private void createStoreBankAccount(Store store, String bankName, String accountNumber, String holder) {
        storeBankAccountRepository.save(StoreBankAccount.builder()
                .store(store)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountHolder(holder)
                .isPrimary(true)
                .isVerified(true)
                .build());
    }

    private void createStoreStaff(Store store, User user, StoreStaffRole role) {
        storeStaffRepository.save(StoreStaff.builder()
                .store(store)
                .user(user)
                .staffRole(role)
                .isPrimary(role == StoreStaffRole.OWNER)
                .build());
    }

    private void createStoreOperationHours(Store store) {
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
            StoreOperationHours.StoreOperationHoursBuilder builder = StoreOperationHours.builder()
                    .store(store)
                    .dayOfWeek(day);

            if (day == DayOfWeek.SUNDAY) {
                builder.isOpen(false);
            } else {
                builder.isOpen(true)
                        .openTime(LocalTime.of(8, 0))
                        .closeTime(isWeekend ? LocalTime.of(15, 0) : LocalTime.of(18, 0))
                        .breakStart(LocalTime.of(12, 0))
                        .breakEnd(LocalTime.of(13, 0));
            }

            storeOperationHoursRepository.save(builder.build());
        }
    }

    private ProductTemplate createProductTemplate(String name, String description) {
        return productTemplateRepository.save(ProductTemplate.builder()
                .name(name)
                .description(description)
                .build());
    }

    private void createCategoryMapping(Category category, ProductTemplate template, int sortOrder) {
        categoryProductTemplateMappingRepository.save(CategoryProductTemplateMapping.builder()
                .category(category)
                .productTemplate(template)
                .sortOrder(sortOrder)
                .build());
    }

    private Product createProduct(String name, String description, String sku,
                                  int price, int stock,
                                  ProductTemplate template, Store store) {
        return productRepository.save(Product.builder()
                .name(name)
                .description(description)
                .sku(sku)
                .price(price)
                .stock(stock)
                .productTemplate(template)
                .store(store)
                .build());
    }

    private void createProductOptions(Product product, String groupName, String... optionNames) {
        ProductOptionGroup group = productOptionGroupRepository.save(ProductOptionGroup.builder()
                .name(groupName)
                .product(product)
                .build());

        for (String optionName : optionNames) {
            productOptionRepository.save(ProductOption.builder()
                    .name(optionName)
                    .productOptionGroup(group)
                    .build());
        }
    }
}
