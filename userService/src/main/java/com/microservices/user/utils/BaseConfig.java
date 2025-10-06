package com.microservices.user.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

// with configuration we create singleton object
@Configuration

// with component we create prototype object
//@Component
public class BaseConfig {

    @Bean
    public MyService myService(){
        return new MyService();
    }
    @Bean
    public MyRunner runner(){
        myService();
        myService();
        return new MyRunner();
    }
}
