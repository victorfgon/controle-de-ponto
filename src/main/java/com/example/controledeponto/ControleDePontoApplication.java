package com.example.controledeponto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class ControleDePontoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControleDePontoApplication.class, args);
	}

}
