package com.skillgap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dao.SkillRepository;
import com.skillgap.dto.SkillTotalCountDto;
import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.dto.response.SkillCoCountDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.JobRoleTag;
import com.skillgap.mapper.ExternalJobOfferMapper;
import com.skillgap.service.JobOfferImportService;
import com.skillgap.service.RoleDictionaryLoader;
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
										ExternalJobOfferMapper externalJobOfferMapper, JobOfferRepository repo, SkillGapService skillGapService, SkillRepository skillrepo
	) {
		return args -> {
			importService.importAll();			
			
		};
	}

}
