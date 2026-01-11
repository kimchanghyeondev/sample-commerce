package com.toycommerce.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.toycommerce.user",
        "com.toycommerce.common.config",
        "com.toycommerce.common.util"
})
@EntityScan(basePackages = "com.toycommerce.common.entity")
@EnableJpaRepositories(basePackages = "com.toycommerce.user.repository")
@EnableJpaAuditing
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}

