package com.toycommerce.gateway.router;

import com.toycommerce.gateway.dto.ProductTemplateDetailDto;
import com.toycommerce.gateway.service.ProductTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@RequiredArgsConstructor
public class ProductTemplateRouter {

    private final ProductTemplateService productTemplateService;

    @Bean
    public RouterFunction<ServerResponse> productTemplateRoutes() {
        return RouterFunctions.route()
                .GET("/api/product-templates/{id}", accept(MediaType.APPLICATION_JSON),
                        request -> {
                            try {
                                Long id = Long.parseLong(request.pathVariable("id"));
                                ProductTemplateDetailDto detail = productTemplateService.getProductTemplateDetail(id);
                                if (detail == null) {
                                    return ServerResponse.notFound().build();
                                }
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(detail));
                            } catch (Exception e) {
                                return ServerResponse.status(500).build();
                            }
                        })
                .build();
    }
}

