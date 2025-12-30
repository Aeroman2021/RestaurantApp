package com.restaurant.app.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return Optional.empty();

            // اینو هم رد کن که "anonymousUser" ذخیره نشه
            String name = auth.getName();
            if (name == null || name.equals("anonymousUser")) return Optional.empty();

            return Optional.of(name);
        };
    }
}
