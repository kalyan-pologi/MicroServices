package com.microservices.user.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


@Configuration
public class ConditionalConfig {

    @Bean
    @Lazy
//    @ConditionalOnProperty(name = "feature.api.enabled", havingValue = "true")
    public ConditionalClient apiClient() {
        return new ConditionalClient(); // Only created if feature.api.enabled=true
    }


}
