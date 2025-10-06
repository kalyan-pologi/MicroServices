package com.microservices.user;

import com.microservices.user.services.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@SpringBootApplication(exclude = {
//		DataSourceAutoConfiguration.class,
//		SecurityAutoConfiguration.class
//})
@EnableDiscoveryClient
//@EnableEurekaClient
@EnableFeignClients
@EnableScheduling
@EnableAsync
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
//		ConfigurableApplicationContext context = SpringApplication.run(UserServiceApplication.class, args);
//		UserService service = context.getBean(UserService.class);
//		System.out.println(service);


	}

}
