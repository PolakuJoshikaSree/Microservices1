package com.quizapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class QuizserviceapigatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizserviceapigatewayApplication.class, args);
	}

}