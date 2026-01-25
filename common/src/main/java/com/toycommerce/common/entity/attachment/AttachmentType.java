package com.toycommerce.common.entity.attachment;

/**
 * 첨부파일 유형
 * 각 도메인에서 사용하는 첨부파일의 용도를 구분
 */
public enum AttachmentType {
    
    // 카테고리 관련
    CATEGORY_LOGO,          // 카테고리 로고
    CATEGORY_BANNER,        // 카테고리 배너
    CATEGORY_ICON,          // 카테고리 아이콘
    
    // 상품 관련
    PRODUCT_MAIN,           // 상품 대표 이미지
    PRODUCT_THUMBNAIL,      // 상품 썸네일
    PRODUCT_DETAIL,         // 상품 상세 이미지
    PRODUCT_GALLERY,        // 상품 갤러리 이미지
    
    // 상품 템플릿 관련
    PRODUCT_TEMPLATE_MAIN,  // 상품 템플릿 대표 이미지
    PRODUCT_TEMPLATE_DETAIL,// 상품 템플릿 상세 이미지
    
    // 리뷰 관련
    REVIEW_IMAGE,           // 리뷰 이미지
    
    // 사용자 관련
    USER_PROFILE,           // 사용자 프로필 이미지
    
    // 기타
    ETC                     // 기타
}

