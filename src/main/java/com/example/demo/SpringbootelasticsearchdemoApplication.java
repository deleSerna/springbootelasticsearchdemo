package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "controller, service")
@SpringBootApplication
public class SpringbootelasticsearchdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootelasticsearchdemoApplication.class,
				args);
	}

}
