package com.skillgap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.skillgap.service.JobOfferImportService;

@SpringBootApplication
public class SkillgapApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillgapApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(JobOfferImportService importService) {
		return args -> {
			importService.importAll();
		};
	}

}
