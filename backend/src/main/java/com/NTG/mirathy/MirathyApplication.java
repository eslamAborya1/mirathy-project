package com.NTG.mirathy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MirathyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MirathyApplication.class, args);
	}

}
