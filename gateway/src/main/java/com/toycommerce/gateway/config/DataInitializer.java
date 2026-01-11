package com.toycommerce.gateway.config;

import com.toycommerce.common.entity.user.Role;
import com.toycommerce.common.entity.category.Category;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.common.entity.product.ProductTemplate;
import com.toycommerce.common.entity.product.ProductOptionGroup;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.gateway.repository.CategoryRepository;
import com.toycommerce.gateway.repository.CategoryProductTemplateMappingRepository;
import com.toycommerce.gateway.repository.ProductTemplateRepository;
import com.toycommerce.gateway.repository.ProductOptionGroupRepository;
import com.toycommerce.gateway.repository.ProductOptionRepository;
import com.toycommerce.gateway.service.UserService;
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
    private final CategoryProductTemplateMappingRepository categoryProductTemplateMappingRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final ProductOptionRepository productOptionRepository;

    @PostConstruct
    @Transactional
    public void init() {
        initUsers();
        initCategories();
        initProductTemplates();
        initProductOptions();
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
            Category electronics = createCategoryIfNotExists("가전·디지털", 1, null);
            Category fashion = createCategoryIfNotExists("패션·의류", 2, null);
            Category toys = createCategoryIfNotExists("완구·취미", 3, null);
            Category food = createCategoryIfNotExists("식품·생활", 4, null);
            Category beauty = createCategoryIfNotExists("뷰티·미용", 5, null);
            Category sports = createCategoryIfNotExists("스포츠·레저", 6, null);

            // 가전·디지털 하위 카테고리
            if (electronics != null) {
                Category smartphone = createCategoryIfNotExists("스마트폰", 1, electronics);
                Category tablet = createCategoryIfNotExists("태블릿", 2, electronics);
                Category tv = createCategoryIfNotExists("TV", 3, electronics);
                Category monitor = createCategoryIfNotExists("모니터", 4, electronics);
                Category laptop = createCategoryIfNotExists("노트북", 5, electronics);
                Category washingMachine = createCategoryIfNotExists("세탁기", 6, electronics);
                Category refrigerator = createCategoryIfNotExists("냉장고", 7, electronics);
                Category airConditioner = createCategoryIfNotExists("에어컨", 8, electronics);
                
                // 스마트폰 하위 카테고리
                if (smartphone != null) {
                    createCategoryIfNotExists("아이폰", 1, smartphone);
                    createCategoryIfNotExists("갤럭시", 2, smartphone);
                    createCategoryIfNotExists("기타 스마트폰", 3, smartphone);
                }
                
                // 세탁기 하위 카테고리
                if (washingMachine != null) {
                    createCategoryIfNotExists("드럼세탁기", 1, washingMachine);
                    createCategoryIfNotExists("통돌이세탁기", 2, washingMachine);
                }
            }

            // 패션·의류 하위 카테고리
            if (fashion != null) {
                Category menFashion = createCategoryIfNotExists("남성의류", 1, fashion);
                Category womenFashion = createCategoryIfNotExists("여성의류", 2, fashion);
                Category shoes = createCategoryIfNotExists("신발", 3, fashion);
                Category bag = createCategoryIfNotExists("가방", 4, fashion);
                Category watch = createCategoryIfNotExists("시계", 5, fashion);
                
                // 남성의류 하위 카테고리
                if (menFashion != null) {
                    createCategoryIfNotExists("티셔츠", 1, menFashion);
                    createCategoryIfNotExists("셔츠", 2, menFashion);
                    createCategoryIfNotExists("바지", 3, menFashion);
                    createCategoryIfNotExists("자켓", 4, menFashion);
                }
                
                // 여성의류 하위 카테고리
                if (womenFashion != null) {
                    createCategoryIfNotExists("원피스", 1, womenFashion);
                    createCategoryIfNotExists("블라우스", 2, womenFashion);
                    createCategoryIfNotExists("스커트", 3, womenFashion);
                    createCategoryIfNotExists("코트", 4, womenFashion);
                }
            }

            // 완구·취미 하위 카테고리
            if (toys != null) {
                Category lego = createCategoryIfNotExists("레고·블록", 1, toys);
                Category doll = createCategoryIfNotExists("인형", 2, toys);
                Category robot = createCategoryIfNotExists("로봇", 3, toys);
                Category puzzle = createCategoryIfNotExists("퍼즐", 4, toys);
            }

            // 식품·생활 하위 카테고리
            if (food != null) {
                createCategoryIfNotExists("과자·간식", 1, food);
                createCategoryIfNotExists("음료", 2, food);
                createCategoryIfNotExists("생활용품", 3, food);
                createCategoryIfNotExists("주방용품", 4, food);
            }

            // 뷰티·미용 하위 카테고리
            if (beauty != null) {
                createCategoryIfNotExists("스킨케어", 1, beauty);
                createCategoryIfNotExists("메이크업", 2, beauty);
                createCategoryIfNotExists("향수", 3, beauty);
                createCategoryIfNotExists("헤어케어", 4, beauty);
            }

            // 스포츠·레저 하위 카테고리
            if (sports != null) {
                createCategoryIfNotExists("운동화", 1, sports);
                createCategoryIfNotExists("운동복", 2, sports);
                createCategoryIfNotExists("캠핑용품", 3, sports);
                createCategoryIfNotExists("자전거", 4, sports);
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
            Category electronics = categoryRepository.findByName("가전·디지털").orElse(null);
            Category fashion = categoryRepository.findByName("패션·의류").orElse(null);
            Category toys = categoryRepository.findByName("완구·취미").orElse(null);
            Category food = categoryRepository.findByName("식품·생활").orElse(null);
            Category beauty = categoryRepository.findByName("뷰티·미용").orElse(null);
            Category sports = categoryRepository.findByName("스포츠·레저").orElse(null);
            
            Category smartphone = categoryRepository.findByName("스마트폰").orElse(null);
            Category tablet = categoryRepository.findByName("태블릿").orElse(null);
            Category tv = categoryRepository.findByName("TV").orElse(null);
            Category monitor = categoryRepository.findByName("모니터").orElse(null);
            Category laptop = categoryRepository.findByName("노트북").orElse(null);
            Category washingMachine = categoryRepository.findByName("세탁기").orElse(null);
            Category refrigerator = categoryRepository.findByName("냉장고").orElse(null);
            Category airConditioner = categoryRepository.findByName("에어컨").orElse(null);
            Category iphone = categoryRepository.findByName("아이폰").orElse(null);
            Category galaxy = categoryRepository.findByName("갤럭시").orElse(null);
            
            Category menFashion = categoryRepository.findByName("남성의류").orElse(null);
            Category womenFashion = categoryRepository.findByName("여성의류").orElse(null);
            Category shoes = categoryRepository.findByName("신발").orElse(null);
            Category bag = categoryRepository.findByName("가방").orElse(null);
            Category watch = categoryRepository.findByName("시계").orElse(null);
            Category menTshirt = categoryRepository.findByName("티셔츠").orElse(null);
            Category menShirt = categoryRepository.findByName("셔츠").orElse(null);
            Category menPants = categoryRepository.findByName("바지").orElse(null);
            Category menJacket = categoryRepository.findByName("자켓").orElse(null);
            Category womenDress = categoryRepository.findByName("원피스").orElse(null);
            Category womenBlouse = categoryRepository.findByName("블라우스").orElse(null);
            Category womenSkirt = categoryRepository.findByName("스커트").orElse(null);
            Category womenCoat = categoryRepository.findByName("코트").orElse(null);
            
            Category lego = categoryRepository.findByName("레고·블록").orElse(null);
            Category doll = categoryRepository.findByName("인형").orElse(null);
            Category robot = categoryRepository.findByName("로봇").orElse(null);
            Category puzzle = categoryRepository.findByName("퍼즐").orElse(null);
            
            Category snack = categoryRepository.findByName("과자·간식").orElse(null);
            Category beverage = categoryRepository.findByName("음료").orElse(null);
            Category skincare = categoryRepository.findByName("스킨케어").orElse(null);
            Category makeup = categoryRepository.findByName("메이크업").orElse(null);
            Category perfume = categoryRepository.findByName("향수").orElse(null);
            Category haircare = categoryRepository.findByName("헤어케어").orElse(null);
            
            Category sneakers = categoryRepository.findByName("운동화").orElse(null);
            Category sportswear = categoryRepository.findByName("운동복").orElse(null);
            Category camping = categoryRepository.findByName("캠핑용품").orElse(null);
            Category bicycle = categoryRepository.findByName("자전거").orElse(null);
            
            Category drumWashing = categoryRepository.findByName("드럼세탁기").orElse(null);
            Category topWashing = categoryRepository.findByName("통돌이세탁기").orElse(null);

            // ========== 가전·디지털 상품 템플릿 ==========
            if (electronics != null) {
                // 스마트폰 - 아이폰
                if (iphone != null) {
                    createProductTemplateAndMapping(iphone, "iPhone 15 Pro 256GB 네추럴 티타늄",
                            "모델명: iPhone 15 Pro, 저장용량: 256GB, 색상: 네추럴 티타늄, 통신사: 자급제, 배터리: 최대 23시간 동영상 재생, 디스플레이: 6.1형 Super Retina XDR, 칩: A17 Pro, 카메라: 48MP 메인/12MP 초광각/12MP 망원", 1);
                    createProductTemplateAndMapping(iphone, "iPhone 15 Pro Max 512GB 블루 티타늄",
                            "모델명: iPhone 15 Pro Max, 저장용량: 512GB, 색상: 블루 티타늄, 통신사: SKT, 배터리: 최대 29시간 동영상 재생, 디스플레이: 6.7형 Super Retina XDR, 칩: A17 Pro", 2);
                    createProductTemplateAndMapping(iphone, "iPhone 15 128GB 핑크",
                            "모델명: iPhone 15, 저장용량: 128GB, 색상: 핑크, 통신사: KT, 배터리: 최대 20시간 동영상 재생, 디스플레이: 6.1형 Super Retina XDR, 칩: A16 Bionic", 3);
                }
                if (smartphone != null) {
                    createProductTemplateAndMapping(smartphone, "갤럭시 S24 Ultra 512GB 타이탄 그레이",
                            "모델명: Galaxy S24 Ultra, 저장용량: 512GB, 색상: 타이탄 그레이, 통신사: 자급제, 배터리: 5000mAh, 디스플레이: 6.8형 Dynamic AMOLED 2X, 칩: Snapdragon 8 Gen 3, S펜 내장, 카메라: 200MP 광각/12MP 초광각/10MP 망원", 1);
                    createProductTemplateAndMapping(smartphone, "갤럭시 S24+ 256GB 타이탄 바이올렛",
                            "모델명: Galaxy S24+, 저장용량: 256GB, 색상: 타이탄 바이올렛, 통신사: LG U+, 배터리: 4900mAh, 디스플레이: 6.7형 Dynamic AMOLED 2X, 칩: Snapdragon 8 Gen 3", 2);
                    createProductTemplateAndMapping(smartphone, "갤럭시 Z 플립5 256GB 라벤더",
                            "모델명: Galaxy Z Flip5, 저장용량: 256GB, 색상: 라벤더, 통신사: 자급제, 배터리: 3700mAh, 디스플레이: 6.7형 메인/3.4형 커버, 폴더블 스마트폰", 3);
                }

                // 태블릿
                if (tablet != null) {
                    createProductTemplateAndMapping(tablet, "iPad Pro 12.9인치 M2 256GB 스페이스 그레이",
                            "모델명: iPad Pro 12.9인치, 칩: Apple M2, 저장용량: 256GB, 색상: 스페이스 그레이, 디스플레이: Liquid Retina XDR, Wi-Fi, Apple Pencil 2세대 지원", 1);
                    createProductTemplateAndMapping(tablet, "iPad Air 11인치 M2 128GB 스타라이트",
                            "모델명: iPad Air 11인치, 칩: Apple M2, 저장용량: 128GB, 색상: 스타라이트, 디스플레이: Liquid Retina, Wi-Fi, Apple Pencil 2세대 지원", 2);
                    createProductTemplateAndMapping(tablet, "갤럭시 탭 S9 Ultra 512GB 그라파이트",
                            "모델명: Galaxy Tab S9 Ultra, 저장용량: 512GB, 색상: 그라파이트, 디스플레이: 14.6형 Dynamic AMOLED 2X, S펜 포함, Wi-Fi", 3);
                }

                // TV
                if (tv != null) {
                    createProductTemplateAndMapping(tv, "LG 올레드 evo C3 65인치",
                            "모델명: OLED C3, 화면 크기: 65인치, 해상도: 4K UHD, 패널: OLED evo, 프로세서: α9 Gen6 AI, HDR: Dolby Vision/HDR10 Pro, 스마트 기능: webOS 23", 1);
                    createProductTemplateAndMapping(tv, "삼성 Neo QLED 8K QN900C 75인치",
                            "모델명: QN900C, 화면 크기: 75인치, 해상도: 8K UHD, 패널: Neo QLED, 프로세서: Neural Quantum Processor 8K, HDR: HDR10+, 스마트 기능: Tizen OS", 2);
                    createProductTemplateAndMapping(tv, "LG 나노셀 4K 55인치",
                            "모델명: NanoCell, 화면 크기: 55인치, 해상도: 4K UHD, 패널: IPS, 프로세서: α7 Gen6, HDR: HDR10 Pro, 스마트 기능: webOS 23", 3);
                }

                // 모니터
                if (monitor != null) {
                    createProductTemplateAndMapping(monitor, "삼성 오디세이 G9 49인치 게이밍 모니터",
                            "모델명: Odyssey G9, 화면 크기: 49인치, 해상도: DQHD 5120x1440, 주사율: 240Hz, 응답속도: 1ms, 패널: VA, 곡률: 1000R, HDR: HDR1000", 1);
                    createProductTemplateAndMapping(monitor, "LG 울트라와이드 34인치 커브드",
                            "모델명: 34WP65C, 화면 크기: 34인치, 해상도: WQHD 3440x1440, 주사율: 75Hz, 응답속도: 5ms, 패널: IPS, 곡률: 1800R", 2);
                    createProductTemplateAndMapping(monitor, "애플 스튜디오 디스플레이 27인치",
                            "모델명: Studio Display, 화면 크기: 27인치, 해상도: 5K 5120x2880, 패널: IPS, 카메라: 12MP 센터 스테이지, 스피커: 6개 스피커 시스템", 3);
                }

                // 노트북
                if (laptop != null) {
                    createProductTemplateAndMapping(laptop, "MacBook Pro 16인치 M3 Max 1TB 스페이스 블랙",
                            "모델명: MacBook Pro 16인치, 칩: Apple M3 Max, 메모리: 36GB, 저장공간: 1TB, 디스플레이: Liquid Retina XDR, 배터리: 최대 22시간, 색상: 스페이스 블랙", 1);
                    createProductTemplateAndMapping(laptop, "MacBook Air 15인치 M3 512GB 미드나이트",
                            "모델명: MacBook Air 15인치, 칩: Apple M3, 메모리: 16GB, 저장공간: 512GB, 디스플레이: Liquid Retina, 배터리: 최대 18시간, 색상: 미드나이트", 2);
                    createProductTemplateAndMapping(laptop, "LG 그램 17인치 17Z90R",
                            "모델명: LG gram 17, 프로세서: Intel Core i7-1360P, 메모리: 16GB, 저장공간: 512GB SSD, 디스플레이: 17인치 WQXGA IPS, 무게: 1.35kg", 3);
                    createProductTemplateAndMapping(laptop, "삼성 갤럭시북3 Pro 360 16인치",
                            "모델명: Galaxy Book3 Pro 360, 프로세서: Intel Core i7-1360P, 메모리: 16GB, 저장공간: 512GB SSD, 디스플레이: 16인치 AMOLED 터치, 360도 회전", 4);
                }

                // 세탁기
                if (washingMachine != null) {
                    if (drumWashing != null) {
                        createProductTemplateAndMapping(drumWashing, "LG 트롬 오브제컬렉션 워시타워 25kg",
                                "모델명: FX25EN, 세탁용량: 25kg, 건조용량: 17kg, 색상: 네이처 베이지, 기능: AI DD 세탁/스팀 건조, 에너지 효율: 1등급, 스마트 기능: ThinQ", 1);
                        createProductTemplateAndMapping(drumWashing, "삼성 비스포크 AI 세탁기 건조기 21kg",
                                "모델명: WD21T, 세탁용량: 21kg, 건조용량: 17kg, 색상: 새틴 베이지, 기능: AI 세탁/스팀 살균, 에너지 효율: 1등급, 스마트 기능: SmartThings", 2);
                    }
                    if (topWashing != null) {
                        createProductTemplateAndMapping(topWashing, "LG 통돌이 세탁기 21kg",
                                "모델명: T21B, 세탁용량: 21kg, 타입: 통돌이, 기능: 6모션 DD, 에너지 효율: 1등급, 소음: 저소음 설계", 1);
                    }
                }

                // 냉장고
                if (refrigerator != null) {
                    createProductTemplateAndMapping(refrigerator, "삼성 비스포크 4도어 냉장고 875L",
                            "모델명: RF85C9000AP, 용량: 875L, 타입: 4도어 프렌치, 색상: 글램 화이트, 기능: AI 절약 모드/독립 냉각, 에너지 효율: 1등급", 1);
                    createProductTemplateAndMapping(refrigerator, "LG 오브제컬렉션 냉장고 832L",
                            "모델명: S834M, 용량: 832L, 타입: 4도어, 색상: 네이처 베이지, 기능: 인스타뷰/스페이스 디자인, 에너지 효율: 1등급", 2);
                }

                // 에어컨
                if (airConditioner != null) {
                    createProductTemplateAndMapping(airConditioner, "삼성 비스포크 무풍에어컨 갤러리 25평형",
                            "모델명: AF25BX934AZRS, 냉방면적: 81.8㎡(25평형), 색상: 새틴 베이지, 기능: 무풍 냉방/AI 쾌적, 에너지 효율: 1등급", 1);
                    createProductTemplateAndMapping(airConditioner, "LG 휘센 스탠드형 에어컨 18평형",
                            "모델명: FQ18VHWW, 냉방면적: 59.5㎡(18평형), 기능: AI 쾌적 모드/스마트 진단, 에너지 효율: 1등급", 2);
                }
            }

            // ========== 패션·의류 상품 템플릿 ==========
            if (fashion != null) {
                // 남성 의류
                if (menTshirt != null) {
                    createProductTemplateAndMapping(menTshirt, "나이키 드라이핏 남성 반팔 티셔츠",
                            "브랜드: 나이키, 소재: 폴리에스터 100%, 사이즈: S/M/L/XL, 색상: 블랙/화이트/그레이, 특징: 땀 흡수 및 건조 기능, 경량 소재", 1);
                    createProductTemplateAndMapping(menTshirt, "아디다스 클래식 로고 티셔츠",
                            "브랜드: 아디다스, 소재: 면 100%, 사이즈: S/M/L/XL, 색상: 네이비/화이트/블랙, 특징: 클래식 로고 디자인, 편안한 착용감", 2);
                }
                if (menShirt != null) {
                    createProductTemplateAndMapping(menShirt, "유니클로 옥스포드 셔츠",
                            "브랜드: 유니클로, 소재: 면 100%, 사이즈: S/M/L/XL, 색상: 화이트/라이트 블루/핑크, 특징: 옥스포드 원단, 정장용/캐주얼 겸용", 1);
                }
                if (menPants != null) {
                    createProductTemplateAndMapping(menPants, "리바이스 511 슬림진",
                            "브랜드: 리바이스, 소재: 면 98% 엘라스테인 2%, 사이즈: 28~36, 색상: 다크 인디고/블랙, 특징: 슬림핏, 스트레치 소재", 1);
                }
                if (menJacket != null) {
                    createProductTemplateAndMapping(menJacket, "노스페이스 나이로비 자켓",
                            "브랜드: 노스페이스, 소재: 나일론, 사이즈: S/M/L/XL, 색상: 블랙/네이비/그레이, 특징: 방수/방풍 기능, 경량", 1);
                }

                // 여성 의류
                if (womenDress != null) {
                    createProductTemplateAndMapping(womenDress, "ZARA 플로럴 패턴 롱 원피스",
                            "브랜드: ZARA, 소재: 면 100%, 사이즈: S/M/L, 색상: 멀티 컬러, 특징: 플로럴 패턴, 롱 기장, 허리 스트링, 계절: 봄/여름", 1);
                    createProductTemplateAndMapping(womenDress, "무신사 스탠다드 미디 원피스",
                            "브랜드: 무신사 스탠다드, 소재: 폴리에스터, 사이즈: S/M/L, 색상: 블랙/베이지/화이트, 특징: 미디 기장, 심플 디자인", 2);
                }
                if (womenBlouse != null) {
                    createProductTemplateAndMapping(womenBlouse, "스파오 린넨 블라우스",
                            "브랜드: 스파오, 소재: 린넨 100%, 사이즈: S/M/L, 색상: 화이트/베이지/핑크, 특징: 통기성 좋은 린넨 소재, 캐주얼/정장 겸용", 1);
                }
                if (womenSkirt != null) {
                    createProductTemplateAndMapping(womenSkirt, "지고트 플리츠 미디 스커트",
                            "브랜드: 지고트, 소재: 폴리에스터, 사이즈: S/M/L, 색상: 블랙/네이비/베이지, 특징: 플리츠 디자인, 미디 기장", 1);
                }
                if (womenCoat != null) {
                    createProductTemplateAndMapping(womenCoat, "마인드브릿지 더블 코트",
                            "브랜드: 마인드브릿지, 소재: 울 80% 폴리에스터 20%, 사이즈: S/M/L, 색상: 블랙/베이지/카멜, 특징: 더블 버튼, 클래식 디자인", 1);
                }

                // 신발
                if (shoes != null) {
                    createProductTemplateAndMapping(shoes, "나이키 에어맥스 90",
                            "브랜드: 나이키, 모델명: Air Max 90, 사이즈: 230~280mm, 색상: 화이트/블랙/레드, 특징: 에어 쿠셔닝, 편안한 착용감, 클래식 디자인", 1);
                    createProductTemplateAndMapping(shoes, "아디다스 울트라부스트 23",
                            "브랜드: 아디다스, 모델명: Ultraboost 23, 사이즈: 230~290mm, 색상: 블랙/화이트/네이비, 특징: BOOST 미드솔, 프라임니트 갑피, 뛰어난 쿠셔닝, 용도: 러닝/일상", 2);
                    createProductTemplateAndMapping(shoes, "컨버스 척 테일러 올스타",
                            "브랜드: 컨버스, 모델명: Chuck Taylor All Star, 사이즈: 230~280mm, 색상: 화이트/블랙/레드, 특징: 클래식 디자인, 캔버스 소재", 3);
                }

                // 가방
                if (bag != null) {
                    createProductTemplateAndMapping(bag, "샤넬 클래식 플랩백 미디엄",
                            "브랜드: 샤넬, 모델명: Classic Flap Bag, 크기: 미디엄, 소재: 램스킨, 색상: 블랙, 특징: 다이아몬드 퀼팅, CC 로고, 체인 스트랩", 1);
                    createProductTemplateAndMapping(bag, "롱샴 르 플리아주 토트백",
                            "브랜드: 롱샴, 모델명: Le Pliage, 타입: 토트백, 소재: 나일론, 색상: 네이비/블랙/베이지, 특징: 접을 수 있는 디자인, 가볍고 실용적", 2);
                    createProductTemplateAndMapping(bag, "샘소나이트 하드케이스 캐리어",
                            "브랜드: 샘소나이트, 타입: 캐리어, 크기: 20인치, 색상: 블랙/실버/네이비, 특징: 하드케이스, 360도 회전 바퀴, TSA 자물쇠", 3);
                }

                // 시계
                if (watch != null) {
                    createProductTemplateAndMapping(watch, "Apple Watch Series 9 45mm 알루미늄",
                            "브랜드: 애플, 모델명: Apple Watch Series 9, 케이스 크기: 45mm, 케이스 재질: 알루미늄, 색상: 미드나이트/스타라이트/레드, 기능: 혈중 산소/심전도/체온 감지, 방수: 50m", 1);
                    createProductTemplateAndMapping(watch, "갤럭시 워치6 클래식 47mm",
                            "브랜드: 삼성, 모델명: Galaxy Watch6 Classic, 케이스 크기: 47mm, 케이스 재질: 스테인리스 스틸, 색상: 블랙/실버, 기능: 심박수/혈중 산소/수면 추적, 방수: 5ATM", 2);
                    createProductTemplateAndMapping(watch, "카시오 G-SHOCK GA-2100",
                            "브랜드: 카시오, 모델명: G-SHOCK GA-2100, 타입: 아날로그 디지털, 색상: 블랙/화이트/레드, 특징: 내구성, 방수 200m, 충격 저항", 3);
                }
            }

            // ========== 완구·취미 상품 템플릿 ==========
            if (toys != null) {
                if (lego != null) {
                    createProductTemplateAndMapping(lego, "레고 클래식 라지 조립 박스 10698",
                            "브랜드: 레고, 모델명: 10698, 조각 수: 790개, 권장 연령: 4세 이상, 테마: 자유 조립, 특징: 다양한 색상과 크기의 브릭, 창의력 향상", 1);
                    createProductTemplateAndMapping(lego, "레고 시티 경찰서 60316",
                            "브랜드: 레고, 모델명: 60316, 조각 수: 292개, 권장 연령: 6세 이상, 테마: 시티, 특징: 경찰서 세트, 미니피겨 포함", 2);
                    createProductTemplateAndMapping(lego, "레고 테크닉 포르쉐 911 42096",
                            "브랜드: 레고, 모델명: 42096, 조각 수: 1580개, 권장 연령: 10세 이상, 테마: 테크닉, 특징: 포르쉐 911 모델, 정교한 조립", 3);
                }
                if (doll != null) {
                    createProductTemplateAndMapping(doll, "바비 인형 패션 세트",
                            "브랜드: 바비, 모델명: 패션ista, 구성: 바비 인형 1개, 의상 3벌, 액세서리, 권장 연령: 3세 이상, 특징: 다양한 스타일 연출 가능", 1);
                    createProductTemplateAndMapping(doll, "핑크퐁 아기상어 인형",
                            "브랜드: 핑크퐁, 모델명: 아기상어 인형, 크기: 30cm, 권장 연령: 3세 이상, 특징: 부드러운 터치감, 귀여운 디자인", 2);
                }
                if (robot != null) {
                    createProductTemplateAndMapping(robot, "또봇 V 마스터 V",
                            "브랜드: 또봇, 모델명: V 마스터 V, 기능: 자동차-로봇 변신, 사운드 효과, 권장 연령: 3세 이상, 특징: 견고한 내구성, 쉬운 변신", 1);
                    createProductTemplateAndMapping(robot, "옵티머스 프라임 트랜스포머",
                            "브랜드: 트랜스포머, 모델명: 옵티머스 프라임, 기능: 트럭-로봇 변신, 권장 연령: 8세 이상, 특징: 정교한 변신 메커니즘", 2);
                }
                if (puzzle != null) {
                    createProductTemplateAndMapping(puzzle, "1000피스 풍경 직소 퍼즐",
                            "조각 수: 1000피스, 완성 크기: 70x50cm, 테마: 아름다운 자연 풍경, 특징: 고품질 인쇄, 정교한 커팅, 권장 연령: 8세 이상", 1);
                    createProductTemplateAndMapping(puzzle, "500피스 고양이 퍼즐",
                            "조각 수: 500피스, 완성 크기: 50x40cm, 테마: 귀여운 고양이, 특징: 밝은 색상, 권장 연령: 6세 이상", 2);
                }
            }

            // ========== 식품·생활 상품 템플릿 ==========
            if (food != null) {
                if (snack != null) {
                    createProductTemplateAndMapping(snack, "오리온 초코파이 정 12개입",
                            "브랜드: 오리온, 제품명: 초코파이 정, 구성: 12개입, 중량: 336g, 특징: 부드러운 마시멜로, 달콤한 초콜릿 코팅, 알레르기: 밀/우유/대두 함유", 1);
                    createProductTemplateAndMapping(snack, "농심 신라면 5입",
                            "브랜드: 농심, 제품명: 신라면, 구성: 5입, 중량: 500g, 특징: 매콤한 맛, 면발 쫄깃함, 알레르기: 밀 함유", 2);
                    createProductTemplateAndMapping(snack, "해태 제크 초콜릿",
                            "브랜드: 해태, 제품명: 제크, 중량: 50g, 특징: 부드러운 초콜릿, 바삭한 과자, 알레르기: 우유/대두 함유", 3);
                }
                if (beverage != null) {
                    createProductTemplateAndMapping(beverage, "코카콜라 500ml 24입",
                            "브랜드: 코카콜라, 용량: 500ml, 구성: 24입, 특징: 클래식한 콜라 맛, 탄산음료", 1);
                    createProductTemplateAndMapping(beverage, "스타벅스 아메리카노 원두",
                            "브랜드: 스타벅스, 제품명: 하우스 블렌드 원두, 중량: 200g, 특징: 중간 로스팅, 균형잡힌 맛, 원산지: 라틴아메리카", 2);
                }
            }

            // ========== 뷰티·미용 상품 템플릿 ==========
            if (beauty != null) {
                if (skincare != null) {
                    createProductTemplateAndMapping(skincare, "라네즈 워터뱅크 블루 히알루론산 크림 50ml",
                            "브랜드: 라네즈, 제품명: 워터뱅크 블루 히알루론산 크림, 용량: 50ml, 피부 타입: 건성/복합성, 특징: 강력한 보습, 피부 장벽 강화, 저자극, 주요 성분: 블루 히알루론산", 1);
                    createProductTemplateAndMapping(skincare, "설화수 자음생 에센스 125ml",
                            "브랜드: 설화수, 제품명: 자음생 에센스, 용량: 125ml, 피부 타입: 모든 피부, 특징: 한방 성분, 피부 진정, 주요 성분: 인삼 추출물", 2);
                    createProductTemplateAndMapping(skincare, "에스티로더 어드밴스드 나이트 리페어 세럼 50ml",
                            "브랜드: 에스티로더, 제품명: 어드밴스드 나이트 리페어 세럼, 용량: 50ml, 특징: 안티에이징, 피부 재생, 주요 성분: 비피더스 발효물", 3);
                }
                if (makeup != null) {
                    createProductTemplateAndMapping(makeup, "맥 립스틱 루비우 3g",
                            "브랜드: 맥, 제품명: 립스틱 루비우, 용량: 3g, 색상: 다양한 컬러, 특징: 매트 피니시, 오래 지속되는 발색", 1);
                    createProductTemplateAndMapping(makeup, "클리오 킬 커버 파운데이션",
                            "브랜드: 클리오, 제품명: 킬 커버 파운데이션, 용량: 30ml, 색상: 다양한 톤, 특징: 높은 커버력, 자연스러운 피니시", 2);
                }
                if (perfume != null) {
                    createProductTemplateAndMapping(perfume, "샤넬 넘버5 오 드 퍼퓸 50ml",
                            "브랜드: 샤넬, 제품명: 넘버5, 용량: 50ml, 타입: 오 드 퍼퓸, 특징: 클래식한 플로럴 향, 지속력: 6-8시간", 1);
                    createProductTemplateAndMapping(perfume, "조 말론 잉글리시 페어 앤 프리지아 코롱 30ml",
                            "브랜드: 조 말론, 제품명: 잉글리시 페어 앤 프리지아, 용량: 30ml, 타입: 코롱, 특징: 신선한 과일 향, 지속력: 4-6시간", 2);
                }
                if (haircare != null) {
                    createProductTemplateAndMapping(haircare, "케라스타즈 엘릭서 울트라 바이탈 100ml",
                            "브랜드: 케라스타즈, 제품명: 엘릭서 울트라 바이탈, 용량: 100ml, 특징: 손상 모발 케어, 영양 공급, 주요 성분: 아르간 오일", 1);
                }
            }

            // ========== 스포츠·레저 상품 템플릿 ==========
            if (sports != null) {
                if (sneakers != null) {
                    createProductTemplateAndMapping(sneakers, "나이키 에어 조던 1 레트로 하이",
                            "브랜드: 나이키, 모델명: Air Jordan 1 Retro High, 사이즈: 230~290mm, 색상: 블랙/화이트/레드, 특징: 클래식 디자인, 에어 쿠셔닝", 1);
                    createProductTemplateAndMapping(sneakers, "뉴발란스 993 그레이",
                            "브랜드: 뉴발란스, 모델명: 993, 사이즈: 230~290mm, 색상: 그레이, 특징: 프리미엄 러닝화, 쿠셔닝, Made in USA", 2);
                }
                if (sportswear != null) {
                    createProductTemplateAndMapping(sportswear, "아디다스 트레이닝 세트",
                            "브랜드: 아디다스, 구성: 상의+하의 세트, 사이즈: S/M/L/XL, 색상: 블랙/네이비/그레이, 특징: 땀 흡수 기능, 편안한 착용감", 1);
                }
                if (camping != null) {
                    createProductTemplateAndMapping(camping, "콜맨 4인용 텐트",
                            "브랜드: 콜맨, 모델명: 4인용 돔 텐트, 크기: 4인용, 특징: 방수 기능, 설치 용이, 통기성 좋음", 1);
                    createProductTemplateAndMapping(camping, "스탠리 어드벤처 쿨러 25L",
                            "브랜드: 스탠리, 모델명: 어드벤처 쿨러, 용량: 25L, 특징: 아이스 보관 3일, 방수 기능, 내구성", 2);
                }
                if (bicycle != null) {
                    createProductTemplateAndMapping(bicycle, "자이언트 TCR 어드밴스드 프로",
                            "브랜드: 자이언트, 모델명: TCR Advanced Pro, 타입: 로드 바이크, 특징: 카본 프레임, 시마노 105 그룹셋, 경량 설계", 1);
                }
            }

            log.info("Product templates initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize product templates", e);
        }
    }

    private void createProductTemplateAndMapping(Category category, String name, String description, int sortOrder) {
        ProductTemplate template = createProductTemplateIfNotExists(name, description, EntityStatus.ACTIVE);
        if (template != null && category != null) {
            createMappingIfNotExists(category, template, sortOrder);
        }
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
        // 이미 매핑이 존재하는지 확인 (간단한 체크)
        boolean exists = categoryProductTemplateMappingRepository.findAll().stream()
                .anyMatch(mapping -> mapping.getCategory().getId().equals(category.getId()) 
                        && mapping.getProductTemplate().getId().equals(productTemplate.getId()));
        
        if (!exists) {
            CategoryProductTemplateMapping mapping = CategoryProductTemplateMapping.builder()
                    .category(category)
                    .productTemplate(productTemplate)
                    .sortOrder(sortOrder)
                    .build();
            
            categoryProductTemplateMappingRepository.save(mapping);
            log.info("Category-ProductTemplate mapping created: {} -> {}", category.getName(), productTemplate.getName());
        }
    }

    private void initProductOptions() {
        try {
            // iPhone 15 Pro 옵션
            ProductTemplate iphone15Pro = productTemplateRepository.findByName("iPhone 15 Pro 256GB 네추럴 티타늄").orElse(null);
            if (iphone15Pro != null) {
                createOptionGroupWithOptions(iphone15Pro, "저장용량", 
                        List.of("128GB", "256GB", "512GB", "1TB"));
                createOptionGroupWithOptions(iphone15Pro, "색상", 
                        List.of("네추럴 티타늄", "블루 티타늄", "화이트 티타늄", "블랙 티타늄"));
                createOptionGroupWithOptions(iphone15Pro, "통신사", 
                        List.of("자급제", "SKT", "KT", "LG U+"));
            }

            // iPhone 15 Pro Max 옵션
            ProductTemplate iphone15ProMax = productTemplateRepository.findByName("iPhone 15 Pro Max 512GB 블루 티타늄").orElse(null);
            if (iphone15ProMax != null) {
                createOptionGroupWithOptions(iphone15ProMax, "저장용량", 
                        List.of("256GB", "512GB", "1TB"));
                createOptionGroupWithOptions(iphone15ProMax, "색상", 
                        List.of("네추럴 티타늄", "블루 티타늄", "화이트 티타늄", "블랙 티타늄"));
                createOptionGroupWithOptions(iphone15ProMax, "통신사", 
                        List.of("자급제", "SKT", "KT", "LG U+"));
            }

            // 갤럭시 S24 Ultra 옵션
            ProductTemplate galaxyS24Ultra = productTemplateRepository.findByName("갤럭시 S24 Ultra 512GB 타이탄 그레이").orElse(null);
            if (galaxyS24Ultra != null) {
                createOptionGroupWithOptions(galaxyS24Ultra, "저장용량", 
                        List.of("256GB", "512GB", "1TB"));
                createOptionGroupWithOptions(galaxyS24Ultra, "색상", 
                        List.of("타이탄 그레이", "타이탄 옐로우", "타이탄 바이올렛", "타이탄 블랙"));
                createOptionGroupWithOptions(galaxyS24Ultra, "통신사", 
                        List.of("자급제", "SKT", "KT", "LG U+"));
            }

            // 갤럭시 S24+ 옵션
            ProductTemplate galaxyS24Plus = productTemplateRepository.findByName("갤럭시 S24+ 256GB 타이탄 바이올렛").orElse(null);
            if (galaxyS24Plus != null) {
                createOptionGroupWithOptions(galaxyS24Plus, "저장용량", 
                        List.of("256GB", "512GB"));
                createOptionGroupWithOptions(galaxyS24Plus, "색상", 
                        List.of("타이탄 그레이", "타이탄 바이올렛", "타이탄 블랙"));
                createOptionGroupWithOptions(galaxyS24Plus, "통신사", 
                        List.of("자급제", "SKT", "KT", "LG U+"));
            }

            // iPad Pro 옵션
            ProductTemplate ipadPro = productTemplateRepository.findByName("iPad Pro 12.9인치 M2 256GB 스페이스 그레이").orElse(null);
            if (ipadPro != null) {
                createOptionGroupWithOptions(ipadPro, "저장용량", 
                        List.of("128GB", "256GB", "512GB", "1TB", "2TB"));
                createOptionGroupWithOptions(ipadPro, "색상", 
                        List.of("스페이스 그레이", "실버"));
                createOptionGroupWithOptions(ipadPro, "연결 방식", 
                        List.of("Wi-Fi", "Wi-Fi + Cellular"));
            }

            // MacBook Pro 옵션
            ProductTemplate macBookPro = productTemplateRepository.findByName("MacBook Pro 16인치 M3 Max 1TB 스페이스 블랙").orElse(null);
            if (macBookPro != null) {
                createOptionGroupWithOptions(macBookPro, "메모리", 
                        List.of("36GB", "48GB", "64GB", "96GB", "128GB"));
                createOptionGroupWithOptions(macBookPro, "저장공간", 
                        List.of("1TB", "2TB", "4TB", "8TB"));
                createOptionGroupWithOptions(macBookPro, "색상", 
                        List.of("스페이스 블랙", "실버"));
            }

            // LG 올레드 TV 옵션
            ProductTemplate lgOled = productTemplateRepository.findByName("LG 올레드 evo C3 65인치").orElse(null);
            if (lgOled != null) {
                createOptionGroupWithOptions(lgOled, "화면 크기", 
                        List.of("55인치", "65인치", "77인치", "83인치"));
            }

            // 삼성 모니터 옵션
            ProductTemplate samsungMonitor = productTemplateRepository.findByName("삼성 오디세이 G9 49인치 게이밍 모니터").orElse(null);
            if (samsungMonitor != null) {
                createOptionGroupWithOptions(samsungMonitor, "색상", 
                        List.of("블랙", "화이트"));
            }

            // 나이키 드라이핏 티셔츠 옵션
            ProductTemplate nikeTshirt = productTemplateRepository.findByName("나이키 드라이핏 남성 반팔 티셔츠").orElse(null);
            if (nikeTshirt != null) {
                createOptionGroupWithOptions(nikeTshirt, "사이즈", 
                        List.of("S", "M", "L", "XL", "XXL"));
                createOptionGroupWithOptions(nikeTshirt, "색상", 
                        List.of("블랙", "화이트", "그레이", "네이비"));
            }

            // ZARA 원피스 옵션
            ProductTemplate zaraDress = productTemplateRepository.findByName("ZARA 플로럴 패턴 롱 원피스").orElse(null);
            if (zaraDress != null) {
                createOptionGroupWithOptions(zaraDress, "사이즈", 
                        List.of("XS", "S", "M", "L"));
                createOptionGroupWithOptions(zaraDress, "색상", 
                        List.of("멀티 컬러", "화이트", "핑크"));
            }

            // 나이키 에어맥스 90 옵션
            ProductTemplate nikeAirMax = productTemplateRepository.findByName("나이키 에어맥스 90").orElse(null);
            if (nikeAirMax != null) {
                createOptionGroupWithOptions(nikeAirMax, "사이즈", 
                        List.of("230mm", "240mm", "250mm", "260mm", "270mm", "280mm"));
                createOptionGroupWithOptions(nikeAirMax, "색상", 
                        List.of("화이트", "블랙", "레드", "네이비"));
            }

            // 샤넬 클래식 플랩백 옵션
            ProductTemplate chanelBag = productTemplateRepository.findByName("샤넬 클래식 플랩백 미디엄").orElse(null);
            if (chanelBag != null) {
                createOptionGroupWithOptions(chanelBag, "크기", 
                        List.of("스몰", "미디엄", "라지"));
                createOptionGroupWithOptions(chanelBag, "소재", 
                        List.of("램스킨", "캐비어 스킨"));
                createOptionGroupWithOptions(chanelBag, "색상", 
                        List.of("블랙", "베이지", "화이트"));
            }

            // Apple Watch 옵션
            ProductTemplate appleWatch = productTemplateRepository.findByName("Apple Watch Series 9 45mm 알루미늄").orElse(null);
            if (appleWatch != null) {
                createOptionGroupWithOptions(appleWatch, "케이스 크기", 
                        List.of("41mm", "45mm"));
                createOptionGroupWithOptions(appleWatch, "케이스 재질", 
                        List.of("알루미늄", "스테인리스 스틸"));
                createOptionGroupWithOptions(appleWatch, "색상", 
                        List.of("미드나이트", "스타라이트", "실버", "레드"));
            }

            // 레고 클래식 옵션
            ProductTemplate legoClassic = productTemplateRepository.findByName("레고 클래식 라지 조립 박스 10698").orElse(null);
            if (legoClassic != null) {
                createOptionGroupWithOptions(legoClassic, "조각 수", 
                        List.of("790개", "1500개", "2210개"));
            }

            // 오리온 초코파이 옵션
            ProductTemplate chocoPie = productTemplateRepository.findByName("오리온 초코파이 정 12개입").orElse(null);
            if (chocoPie != null) {
                createOptionGroupWithOptions(chocoPie, "구성", 
                        List.of("12개입", "24개입", "36개입"));
            }

            // 라네즈 크림 옵션
            ProductTemplate laneigeCream = productTemplateRepository.findByName("라네즈 워터뱅크 블루 히알루론산 크림 50ml").orElse(null);
            if (laneigeCream != null) {
                createOptionGroupWithOptions(laneigeCream, "용량", 
                        List.of("50ml", "100ml"));
            }

            // 나이키 에어 조던 옵션
            ProductTemplate airJordan = productTemplateRepository.findByName("나이키 에어 조던 1 레트로 하이").orElse(null);
            if (airJordan != null) {
                createOptionGroupWithOptions(airJordan, "사이즈", 
                        List.of("230mm", "240mm", "250mm", "260mm", "270mm", "280mm", "290mm"));
                createOptionGroupWithOptions(airJordan, "색상", 
                        List.of("블랙/화이트/레드", "화이트/블랙", "블랙/레드"));
            }

            log.info("Product options initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize product options", e);
        }
    }

    private void createOptionGroupWithOptions(ProductTemplate productTemplate, String groupName, List<String> optionNames) {
        if (productTemplate == null) {
            return;
        }

        // OptionGroup 생성 또는 조회
        ProductOptionGroup optionGroup = productOptionGroupRepository
                .findByProductTemplateIdAndName(productTemplate.getId(), groupName)
                .orElseGet(() -> {
                    ProductOptionGroup newGroup = ProductOptionGroup.builder()
                            .productTemplate(productTemplate)
                            .name(groupName)
                            .entityStatus(EntityStatus.ACTIVE)
                            .build();
                    ProductOptionGroup saved = productOptionGroupRepository.save(newGroup);
                    log.info("ProductOptionGroup created: {} for {}", groupName, productTemplate.getName());
                    return saved;
                });

        // Options 생성
        for (String optionName : optionNames) {
            if (!productOptionRepository.existsByProductTemplateIdAndName(productTemplate.getId(), optionName)) {
                ProductOption option = ProductOption.builder()
                        .productTemplate(productTemplate)
                        .productOptionGroup(optionGroup)
                        .name(optionName)
                        .entityStatus(EntityStatus.ACTIVE)
                        .build();
                productOptionRepository.save(option);
                log.info("ProductOption created: {} for {} in group {}", optionName, productTemplate.getName(), groupName);
            }
        }
    }
}

