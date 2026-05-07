package com.skillgap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.service.provider.JobOffersProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobOfferImportService {

    private final List<JobOffersProvider> providers;
    private final JobOfferService jobOfferService;
    private final JobOfferRepository jobOfferRepository;

    @Transactional
    public void importAll() {
        System.out.println("DEBUG: Import start: ");
        Set<String> existingIdInDb = jobOfferRepository.findAllExternalIds();

        for (JobOffersProvider provider : providers) {
            try {
                log.info("Fetching offers from provider: {}", provider.getClass().getSimpleName());
                List<JobOfferDto> dtos = provider.fetchAll();
                List<JobOffer> offersToSave = new ArrayList<>();

                for (JobOfferDto jobofferDto : dtos) {
                    if (!existingIdInDb.contains(jobofferDto.getGuid())) {
                        JobOffer entity = jobOfferService.mapFromDto(jobofferDto);
                        offersToSave.add(entity);
                        existingIdInDb.add(jobofferDto.getGuid());
                    }
                }
                jobOfferRepository.saveAll(offersToSave);
                log.info("{} new offers saved from provider", offersToSave.size());

            } catch (Exception e) {
                log.error("Failed to import offers from provider: {}. Error: {}", provider.getClass(), e.getMessage());
            }
        }
    }

}
