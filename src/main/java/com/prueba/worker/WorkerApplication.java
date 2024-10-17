package com.prueba.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WorkerApplication {

	public static void main(String[] args) {

		SpringApplicationBuilder builder = new SpringApplicationBuilder(WorkerApplication.class);
		builder.properties("spring.config.name:pedidos-worker")
				.build()
				.run(args);
	}

}
