package com.microservices.user.utils;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // To use restTemplate as autowired we need to create Bean for it
    @Bean
    // loadBalanced is used to replace port number to service name in restTemplate
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}

