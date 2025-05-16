package com.adamant.storemicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class StoremicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoremicroserviceApplication.class, args);
	}

}
