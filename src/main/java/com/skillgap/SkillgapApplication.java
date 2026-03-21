package com.skillgap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.service.JobOfferImportService;
import com.skillgap.service.SkillDictionaryLoader;
import com.skillgap.service.SkillExtractionService;

@SpringBootApplication
public class SkillgapApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillgapApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(JobOfferImportService importService, SkillExtractionService service, SkillDictionaryLoader loader) {
		return args -> {

		};
	}

}
