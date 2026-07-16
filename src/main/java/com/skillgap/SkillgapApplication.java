package com.skillgap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dao.SkillRepository;
import com.skillgap.entity.enums.JobRoleTag;
import com.skillgap.mapper.ExternalJobOfferMapper;
import com.skillgap.service.JobOfferImportService;
import com.skillgap.service.RoleDictionaryLoader;
import com.skillgap.service.SkillAnalysisService;
import com.skillgap.service.SkillDictionaryLoader;
import com.skillgap.service.SkillGapService;
import com.skillgap.service.SkillService;

@SpringBootApplication
public class SkillgapApplication {

    public static void main(String[] args) {
		SpringApplication.run(SkillgapApplication.class, args);
	}

	@Bean
	// @Profile("!test")
	CommandLineRunner commandLineRunner(JobOfferImportService importService, SkillService service, SkillDictionaryLoader loader, RoleDictionaryLoader roleDictionaryLoader,
										ExternalJobOfferMapper externalJobOfferMapper, JobOfferRepository repo, SkillGapService skillGapService, SkillRepository skillrepo,
										SkillAnalysisService skillAnalysisService
	) {
		return args -> {
			importService.importAll();	
		
			
		};
	}

}
