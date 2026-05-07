package com.skillgap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.mapper.ExternalJobOfferMapper;
import com.skillgap.service.JobOfferImportService;
import com.skillgap.service.RoleDictionaryLoader;
import com.skillgap.service.SkillDictionaryLoader;
import com.skillgap.service.SkillService;

@SpringBootApplication
public class SkillgapApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillgapApplication.class, args);
	}

	@Bean
	// @Profile("!test")
	CommandLineRunner commandLineRunner(JobOfferImportService importService, SkillService service, SkillDictionaryLoader loader, RoleDictionaryLoader roleDictionaryLoader,
										ExternalJobOfferMapper externalJobOfferMapper
	) {
		return args -> {
			importService.importAll();



			// JobOfferDto jo = new JobOfferDto();
			// jo.setTitle("Java developer junior spring");

			// JobOffer mapped = externalJobOfferMapper.mapToJobOffer(jo, new HashSet<>());
			// System.out.println("Offer title: " + mapped.getTitle());
			// System.out.println("Offer role: " + mapped.getRoleTag());
			
			
		};
	}

}
