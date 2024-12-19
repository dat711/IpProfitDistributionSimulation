package com.LegalEntitiesManagement.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class V1Application {
	public static void main(String[] args) {
		SpringApplication.run(V1Application.class, args);
	}

}
