package com.order.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.order.manager.repository")
@EntityScan("com.order.manager.model")
@SpringBootApplication
public class OrderSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderSystemApplication.class, args);
	}

}
