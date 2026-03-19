package com.toycommerce.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.toycommerce.admin",
        "com.toycommerce.common.config"
})
@EntityScan(basePackages = "com.toycommerce.common.entity")
@EnableJpaRepositories(basePackages = "com.toycommerce.admin.repository")
@EnableJpaAuditing
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}

