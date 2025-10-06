package com.microservices.user;

import com.microservices.user.Utils.Rectangle;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceApplicationTests {

	@Test
	void shouldCalculateAreaCorrectly() {
		Rectangle rectangle = new Rectangle(5,4);
		double area = rectangle.calculateArea();
		assertEquals(20.0, area);
	}

}
