package com.toycommerce.gateway.router;

import com.toycommerce.gateway.dto.CategoryDto;
import com.toycommerce.gateway.service.CategoryService;
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
public class CategoryRouter {

    private final CategoryService categoryService;

    @Bean
    public RouterFunction<ServerResponse> categoryRoutes() {
        return RouterFunctions.route()
                .GET("/api/categories", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                List<CategoryDto> categories = categoryService.getAllCategories();
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(categories));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .GET("/api/categories/roots", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                List<CategoryDto> categories = categoryService.getRootCategories();
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(categories));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .GET("/api/categories/tree", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                List<CategoryDto> categories = categoryService.getCategoryTree();
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(categories));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .GET("/api/categories/{id}", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                Long id = Long.parseLong(request.pathVariable("id"));
                                CategoryDto category = categoryService.getCategoryById(id);
                                if (category == null) {
                                    return ServerResponse.notFound().build();
                                }
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(category));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .GET("/api/categories/parent/{parentId}", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                Long parentId = Long.parseLong(request.pathVariable("parentId"));
                                List<CategoryDto> categories = categoryService.getCategoriesByParentId(parentId);
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(categories));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .build();
    }
}

