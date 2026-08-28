package com.skillgap;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dao.SkillRepository;
import com.skillgap.entity.enums.JobRoleTag;
import com.skillgap.integration.common.JobOfferDto;
import com.skillgap.mapper.ExternalJobOfferMapper;
import com.skillgap.service.JobOfferImportService;
import com.skillgap.service.RoleDictionaryLoader;
import com.skillgap.service.SkillAnalysisService;
import com.skillgap.service.SkillDictionaryLoader;
import com.skillgap.service.SkillGapService;
import com.skillgap.service.SkillService;
import com.skillgap.service.provider.JustJoinItOffersProvider;

@SpringBootApplication
@Slf4j
public class SkillgapApplication {

	private final JustJoinItOffersProvider joinItClient;
	private final JobOfferImportService importService;

	public SkillgapApplication(JustJoinItOffersProvider joinItClient, JobOfferImportService importService) {
        this.joinItClient = joinItClient;
		this.importService = importService;
    }

    public static void main(String[] args) {
		SpringApplication.run(SkillgapApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		log.info("Application ready - fetching offers...");
		try {
			// List<JobOfferDto> offers = this.joinItClient.fetchAll();
			importService.importAll();
			

		} catch (Exception e) {
			log.error("Error during offers fetching: ", e);
		}

	}

}
