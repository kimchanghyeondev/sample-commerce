package com.toycommerce.user.config;

import com.toycommerce.common.entity.user.Role;
import com.toycommerce.common.entity.user.User;
import com.toycommerce.common.entity.category.Category;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.common.entity.product.ProductTemplate;
import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductOptionGroup;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.user.repository.CategoryRepository;
import com.toycommerce.user.repository.CategoryProductTemplateMappingRepository;
import com.toycommerce.user.repository.ProductTemplateRepository;
import com.toycommerce.user.repository.ProductRepository;
import com.toycommerce.user.repository.ProductOptionGroupRepository;
import com.toycommerce.user.repository.ProductOptionRepository;
import com.toycommerce.user.repository.CartRepository;
import com.toycommerce.user.service.UserService;
import com.toycommerce.common.entity.cart.Cart;
import com.toycommerce.common.entity.cart.CartItem;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductRepository productRepository;
    private final CategoryProductTemplateMappingRepository categoryProductTemplateMappingRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CartRepository cartRepository;

    @PostConstruct
    @Transactional
    public void init() {
        initUsers();
        initCategories();
        initProductTemplates();
        initProducts();
        initProductOptions();
        initCart();
    }

    private void initUsers() {
        try {
            if (!userService.existsByUsername("admin")) {
                userService.createUser("admin", "admin", Role.ADMIN);
                log.info("Admin user created: admin/admin");
            }
            if (!userService.existsByUsername("user")) {
                userService.createUser("user", "user", Role.USER);
                log.info("User created: user/user");
            }
        } catch (Exception e) {
            log.error("Failed to initialize users", e);
        }
    }

    private void initCategories() {
        try {
            // 루트 카테고리 생성
            Category seafood = createCategoryIfNotExists("수산물", 1, null);

            // 수산물 하위 카테고리
            if (seafood != null) {
                Category fish = createCategoryIfNotExists("생선류", 1, seafood);
                Category shellfish = createCategoryIfNotExists("조개류", 2, seafood);
                Category shrimpCrab = createCategoryIfNotExists("새우·게류", 3, seafood);
                
                // 생선류 하위 카테고리
                if (fish != null) {
                    createCategoryIfNotExists("갈치", 1, fish);
                    createCategoryIfNotExists("고등어", 2, fish);
                    createCategoryIfNotExists("연어", 3, fish);
                }
                
                // 조개류 하위 카테고리
                if (shellfish != null) {
                    createCategoryIfNotExists("홍합", 1, shellfish);
                    createCategoryIfNotExists("전복", 2, shellfish);
                }
                
                // 새우·게류 하위 카테고리
                if (shrimpCrab != null) {
                    createCategoryIfNotExists("새우", 1, shrimpCrab);
                    createCategoryIfNotExists("게", 2, shrimpCrab);
                }
            }

            log.info("Categories initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize categories", e);
        }
    }

    private Category createCategoryIfNotExists(String name, Integer displayOrder, Category parent) {
        if (!categoryRepository.existsByName(name)) {
            Category category = Category.builder()
                    .name(name)
                    .displayOrder(displayOrder)
                    .enabled(true)
                    .build();
            
            if (parent != null) {
                parent.addChild(category);
            }
            
            Category saved = categoryRepository.save(category);
            log.info("Category created: {} (parent: {})", name, parent != null ? parent.getName() : "root");
            return saved;
        }
        return categoryRepository.findByName(name).orElse(null);
    }

    private void initProductTemplates() {
        try {
            // 카테고리 조회
            Category cutlassfish = categoryRepository.findByName("갈치").orElse(null);
            Category mackerel = categoryRepository.findByName("고등어").orElse(null);
            Category salmon = categoryRepository.findByName("연어").orElse(null);
            Category mussel = categoryRepository.findByName("홍합").orElse(null);
            Category abalone = categoryRepository.findByName("전복").orElse(null);
            Category shrimp = categoryRepository.findByName("새우").orElse(null);
            Category crab = categoryRepository.findByName("게").orElse(null);

            // ========== ProductTemplate 생성 (총 7개) ==========
            // 갈치
            if (cutlassfish != null) {
                ProductTemplate cutlassfishTemplate = createProductTemplateIfNotExists("갈치",
                        "신선한 갈치, 다양한 조리법으로 즐길 수 있는 제품", EntityStatus.ACTIVE);
                if (cutlassfishTemplate != null) {
                    createMappingIfNotExists(cutlassfish, cutlassfishTemplate, 1);
                }
            }

            // 고등어
            if (mackerel != null) {
                ProductTemplate mackerelTemplate = createProductTemplateIfNotExists("고등어",
                        "신선한 고등어, 구이용으로 최적, 영양성분: 오메가3 풍부", EntityStatus.ACTIVE);
                if (mackerelTemplate != null) {
                    createMappingIfNotExists(mackerel, mackerelTemplate, 1);
                }
            }

            // 연어
            if (salmon != null) {
                ProductTemplate salmonTemplate = createProductTemplateIfNotExists("연어",
                        "신선한 연어, 회용으로 최적, 영양성분: 오메가3 풍부", EntityStatus.ACTIVE);
                if (salmonTemplate != null) {
                    createMappingIfNotExists(salmon, salmonTemplate, 1);
                }
            }

            // 홍합
            if (mussel != null) {
                ProductTemplate musselTemplate = createProductTemplateIfNotExists("홍합",
                        "손질된 홍합, 찜/탕용, 영양성분: 철분 풍부", EntityStatus.ACTIVE);
                if (musselTemplate != null) {
                    createMappingIfNotExists(mussel, musselTemplate, 1);
                }
            }

            // 전복
            if (abalone != null) {
                ProductTemplate abaloneTemplate = createProductTemplateIfNotExists("전복",
                        "손질된 전복, 구이/찜용, 영양성분: 아연 풍부", EntityStatus.ACTIVE);
                if (abaloneTemplate != null) {
                    createMappingIfNotExists(abalone, abaloneTemplate, 1);
                }
            }

            // 새우
            if (shrimp != null) {
                ProductTemplate shrimpTemplate = createProductTemplateIfNotExists("새우",
                        "손질된 새우, 튀김/찜용, 영양성분: 단백질 풍부", EntityStatus.ACTIVE);
                if (shrimpTemplate != null) {
                    createMappingIfNotExists(shrimp, shrimpTemplate, 1);
                }
            }

            // 게
            if (crab != null) {
                ProductTemplate crabTemplate = createProductTemplateIfNotExists("게",
                        "손질된 게, 찜/탕용, 영양성분: 단백질 풍부", EntityStatus.ACTIVE);
                if (crabTemplate != null) {
                    createMappingIfNotExists(crab, crabTemplate, 1);
                }
            }

            log.info("Product templates initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize product templates", e);
        }
    }

    private void initProducts() {
        try {
            // ProductTemplate 조회
            ProductTemplate cutlassfishTemplate = productTemplateRepository.findByName("갈치").orElse(null);
            ProductTemplate mackerelTemplate = productTemplateRepository.findByName("고등어").orElse(null);
            ProductTemplate salmonTemplate = productTemplateRepository.findByName("연어").orElse(null);
            ProductTemplate musselTemplate = productTemplateRepository.findByName("홍합").orElse(null);
            ProductTemplate abaloneTemplate = productTemplateRepository.findByName("전복").orElse(null);
            ProductTemplate shrimpTemplate = productTemplateRepository.findByName("새우").orElse(null);
            ProductTemplate crabTemplate = productTemplateRepository.findByName("게").orElse(null);

            // ========== Product 생성 (총 10개) ==========
            // 갈치
            if (cutlassfishTemplate != null) {
                createProductIfNotExists(cutlassfishTemplate, "갈치조림", "갈치를 양념에 조린 제품", 
                        "CUTLASSFISH-JORIM-001", 20000, 100, EntityStatus.ACTIVE);
                createProductIfNotExists(cutlassfishTemplate, "갈치튀김", "갈치를 튀긴 제품", 
                        "CUTLASSFISH-FRY-001", 18000, 100, EntityStatus.ACTIVE);
            }

            // 고등어
            if (mackerelTemplate != null) {
                createProductIfNotExists(mackerelTemplate, "고등어구이", "신선한 고등어를 구운 제품", 
                        "MACKEREL-GUI-001", 15000, 100, EntityStatus.ACTIVE);
            }

            // 연어
            if (salmonTemplate != null) {
                createProductIfNotExists(salmonTemplate, "연어회", "신선한 연어 회", 
                        "SALMON-SASHIMI-001", 35000, 100, EntityStatus.ACTIVE);
            }

            // 홍합
            if (musselTemplate != null) {
                createProductIfNotExists(musselTemplate, "홍합찜", "홍합을 찐 제품", 
                        "MUSSEL-JJIM-001", 12000, 100, EntityStatus.ACTIVE);
            }

            // 전복
            if (abaloneTemplate != null) {
                createProductIfNotExists(abaloneTemplate, "전복구이", "전복을 구운 제품", 
                        "ABALONE-GUI-001", 45000, 100, EntityStatus.ACTIVE);
            }

            // 새우
            if (shrimpTemplate != null) {
                createProductIfNotExists(shrimpTemplate, "새우튀김", "새우를 튀긴 제품", 
                        "SHRIMP-FRY-001", 15000, 100, EntityStatus.ACTIVE);
            }

            // 게
            if (crabTemplate != null) {
                createProductIfNotExists(crabTemplate, "게찜", "게를 찐 제품", 
                        "CRAB-JJIM-001", 30000, 100, EntityStatus.ACTIVE);
            }

            log.info("Products initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize products", e);
        }
    }

    private void initProductOptions() {
        try {
            // ========== ProductOptionGroup 및 ProductOption 생성 ==========
            // 갈치조림 옵션
            Product cutlassfishJorim = productRepository.findBySku("CUTLASSFISH-JORIM-001").orElse(null);
            if (cutlassfishJorim != null) {
                createOptionGroupWithOptions(cutlassfishJorim, "조리 여부", 
                        List.of("조리O", "조리X"));
                createOptionGroupWithOptions(cutlassfishJorim, "중량", 
                        List.of("500g", "1kg"));
            }

            // 갈치튀김 옵션
            Product cutlassfishFry = productRepository.findBySku("CUTLASSFISH-FRY-001").orElse(null);
            if (cutlassfishFry != null) {
                createOptionGroupWithOptions(cutlassfishFry, "조리 여부", 
                        List.of("조리O", "조리X"));
                createOptionGroupWithOptions(cutlassfishFry, "중량", 
                        List.of("500g", "1kg"));
            }

            // 고등어구이 옵션
            Product mackerelGui = productRepository.findBySku("MACKEREL-GUI-001").orElse(null);
            if (mackerelGui != null) {
                createOptionGroupWithOptions(mackerelGui, "조리 여부", 
                        List.of("조리O", "조리X"));
                createOptionGroupWithOptions(mackerelGui, "중량", 
                        List.of("500g", "1kg"));
            }

            // 연어회 옵션
            Product salmonSashimi = productRepository.findBySku("SALMON-SASHIMI-001").orElse(null);
            if (salmonSashimi != null) {
                createOptionGroupWithOptions(salmonSashimi, "부위", 
                        List.of("등살", "뱃살", "혼합"));
                createOptionGroupWithOptions(salmonSashimi, "원산지", 
                        List.of("노르웨이", "칠레", "캐나다"));
            }

            // 홍합찜 옵션
            Product musselJjim = productRepository.findBySku("MUSSEL-JJIM-001").orElse(null);
            if (musselJjim != null) {
                createOptionGroupWithOptions(musselJjim, "조리 여부", 
                        List.of("조리O", "조리X"));
                createOptionGroupWithOptions(musselJjim, "중량", 
                        List.of("500g", "1kg"));
            }

            // 전복구이 옵션
            Product abaloneGui = productRepository.findBySku("ABALONE-GUI-001").orElse(null);
            if (abaloneGui != null) {
                createOptionGroupWithOptions(abaloneGui, "조리 여부", 
                        List.of("조리O", "조리X"));
                createOptionGroupWithOptions(abaloneGui, "크기", 
                        List.of("소", "중", "대"));
            }

            // 새우튀김 옵션
            Product shrimpFry = productRepository.findBySku("SHRIMP-FRY-001").orElse(null);
            if (shrimpFry != null) {
                createOptionGroupWithOptions(shrimpFry, "조리 여부", 
                        List.of("조리O", "조리X"));
                createOptionGroupWithOptions(shrimpFry, "중량", 
                        List.of("300g", "500g", "1kg"));
            }

            // 게찜 옵션
            Product crabJjim = productRepository.findBySku("CRAB-JJIM-001").orElse(null);
            if (crabJjim != null) {
                createOptionGroupWithOptions(crabJjim, "조리 여부", 
                        List.of("조리O", "조리X"));
                createOptionGroupWithOptions(crabJjim, "마리 수", 
                        List.of("1마리", "2마리", "3마리"));
            }

            log.info("Product options initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize product options", e);
        }
    }

    private Product createProductIfNotExists(ProductTemplate productTemplate, String name, String description, 
                                             String sku, Integer price, Integer stock, EntityStatus status) {
        if (!productRepository.existsBySku(sku)) {
            Product product = Product.builder()
                    .productTemplate(productTemplate)
                    .name(name)
                    .description(description)
                    .sku(sku)
                    .price(price)
                    .stock(stock)
                    .status(status)
                    .build();
            
            Product saved = productRepository.save(product);
            log.info("Product created: {} (template: {})", name, productTemplate.getName());
            return saved;
        }
        return productRepository.findBySku(sku).orElse(null);
    }

    private ProductTemplate createProductTemplateIfNotExists(String name, String description, EntityStatus status) {
        if (!productTemplateRepository.existsByName(name)) {
            ProductTemplate template = ProductTemplate.builder()
                    .name(name)
                    .description(description)
                    .status(status)
                    .build();
            
            ProductTemplate saved = productTemplateRepository.save(template);
            log.info("Product template created: {}", name);
            return saved;
        }
        return productTemplateRepository.findByName(name).orElse(null);
    }

    private void createMappingIfNotExists(Category category, ProductTemplate productTemplate, int sortOrder) {
        java.util.Optional<CategoryProductTemplateMapping> existing = categoryProductTemplateMappingRepository
                .findByCategoryAndProductTemplate(category, productTemplate);
        
        if (existing.isEmpty()) {
            CategoryProductTemplateMapping mapping = CategoryProductTemplateMapping.builder()
                    .category(category)
                    .productTemplate(productTemplate)
                    .sortOrder(sortOrder)
                    .build();
            
            categoryProductTemplateMappingRepository.save(mapping);
            log.info("Category-ProductTemplate mapping created: {} -> {}", category.getName(), productTemplate.getName());
        }
    }

    private void createOptionGroupWithOptions(Product product, String groupName, List<String> optionNames) {
        if (product == null) {
            return;
        }

        // OptionGroup 생성 또는 조회
        ProductOptionGroup optionGroup = productOptionGroupRepository
                .findByProductIdAndName(product.getId(), groupName)
                .orElseGet(() -> {
                    ProductOptionGroup newGroup = ProductOptionGroup.builder()
                            .product(product)
                            .name(groupName)
                            .status(EntityStatus.ACTIVE)
                            .build();
                    ProductOptionGroup saved = productOptionGroupRepository.save(newGroup);
                    log.info("ProductOptionGroup created: {} for {}", groupName, product.getName());
                    return saved;
                });

        // Options 생성
        for (String optionName : optionNames) {
            if (!productOptionRepository.existsByProductOptionGroupIdAndName(optionGroup.getId(), optionName)) {
                ProductOption option = ProductOption.builder()
                        .productOptionGroup(optionGroup)
                        .name(optionName)
                        .status(EntityStatus.ACTIVE)
                        .build();
                productOptionRepository.save(option);
                log.info("ProductOption created: {} for {} in group {}", optionName, product.getName(), groupName);
            }
        }
    }

    private void initCart() {
        try {
            User user = userService.findByUsername("user")
                    .orElseThrow(() -> new RuntimeException("User 'user' not found"));

            // 기존 장바구니가 있으면 스킵
            if (cartRepository.existsByUser(user)) {
                log.info("Cart already exists for user: user");
                return;
            }

            // ProductOption 2개 조회 (최소 2개가 있어야 함)
            List<ProductOption> productOptions = productOptionRepository.findAll();
            if (productOptions.size() < 2) {
                log.warn("Not enough ProductOptions found. Need at least 2, found: {}", productOptions.size());
                return;
            }

            // 장바구니 생성
            Cart cart = cartRepository.save(Cart.builder()
                    .user(user)
                    .build());

            // 장바구니 아이템 2개 추가
            CartItem cartItem1 = CartItem.builder()
                    .cart(cart)
                    .productOption(productOptions.get(0))
                    .quantity(1)
                    .build();

            CartItem cartItem2 = CartItem.builder()
                    .cart(cart)
                    .productOption(productOptions.get(1))
                    .quantity(2)
                    .build();

            cart.getCartItems().add(cartItem1);
            cart.getCartItems().add(cartItem2);

            cartRepository.save(cart);
            log.info("Cart initialized with 2 items for user: user");
        } catch (Exception e) {
            log.error("Failed to initialize cart", e);
        }
    }
}
