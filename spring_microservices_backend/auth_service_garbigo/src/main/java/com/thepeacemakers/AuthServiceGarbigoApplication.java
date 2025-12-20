package com.thepeacemakers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuthServiceGarbigoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceGarbigoApplication.class, args);
	}

}
