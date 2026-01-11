package com.toycommerce.gateway.router;

import com.toycommerce.gateway.dto.CategoryProductTemplateDto;
import com.toycommerce.gateway.dto.CategoryWithProductTemplatesDto;
import com.toycommerce.gateway.service.CategoryProductTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
@RequiredArgsConstructor
public class CategoryProductTemplateRouter {

    private final CategoryProductTemplateService categoryProductTemplateService;

    @Bean
    public RouterFunction<ServerResponse> categoryProductTemplateRoutes() {
        return RouterFunctions.route()
                // 카테고리별로 그룹화된 ProductTemplate 목록 조회 (1:N 구조 명확히 표현)
                .GET("/api/category-product-templates/grouped", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                List<CategoryWithProductTemplatesDto> grouped = categoryProductTemplateService.getGroupedByCategory();
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(grouped));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                // 특정 카테고리의 ProductTemplate 목록 조회 (1:N 구조)
                .GET("/api/category-product-templates/category/{categoryId}/grouped", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                Long categoryId = Long.parseLong(request.pathVariable("categoryId"));
                                CategoryWithProductTemplatesDto grouped = categoryProductTemplateService.getByCategoryIdGrouped(categoryId);
                                if (grouped == null) {
                                    return ServerResponse.notFound().build();
                                }
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(grouped));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                // 기존 API (플랫 리스트 형태)
                .GET("/api/category-product-templates", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                List<CategoryProductTemplateDto> templates = categoryProductTemplateService.getAllEnabled();
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(templates));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .GET("/api/category-product-templates/category/{categoryId}", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                Long categoryId = Long.parseLong(request.pathVariable("categoryId"));
                                List<CategoryProductTemplateDto> templates = categoryProductTemplateService.getByCategoryId(categoryId);
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(templates));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .GET("/api/category-product-templates/parent/{parentCategoryId}", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                Long parentCategoryId = Long.parseLong(request.pathVariable("parentCategoryId"));
                                List<CategoryProductTemplateDto> templates = categoryProductTemplateService.getByParentCategoryId(parentCategoryId);
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(templates));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .build();
    }
}

