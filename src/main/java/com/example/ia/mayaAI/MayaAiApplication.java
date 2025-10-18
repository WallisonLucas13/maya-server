package com.example.ia.mayaAI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MayaAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MayaAiApplication.class, args);
	}

}
