package com.skillgap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dao.SkillRepository;
import com.skillgap.service.JobOfferLoader;

@SpringBootApplication
public class SkillgapApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillgapApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(JobOfferLoader dataLoader) {
		return args -> {
			dataLoader.loadInitialData();
		};
	}

}
