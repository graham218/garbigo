package com.garbigo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableMongoAuditing
public class GarbigoAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GarbigoAuthServiceApplication.class, args);
	}

}
