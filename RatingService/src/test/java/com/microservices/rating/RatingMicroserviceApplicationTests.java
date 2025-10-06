package com.microservices.rating;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // 👈 tells Spring to load application-test.yml
class RatingMicroserviceApplicationTests {

}
