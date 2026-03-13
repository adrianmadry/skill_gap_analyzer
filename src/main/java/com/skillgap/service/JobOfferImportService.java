package com.skillgap.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.mapper.ExternalJobOfferMapper;
import com.skillgap.service.provider.JobOffersProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobOfferImportService {

    private final List<JobOffersProvider> providers;
    private final ExternalJobOfferMapper mapper;
    private final JobOfferRepository jobOfferRepository;

    public void importAll() {
        for (JobOffersProvider provider : providers) {
            try {
                log.info("Fetching offers from provider: {}", provider.getClass().getSimpleName());
                List<JobOfferDto> dtos = provider.fetchAll();

                List<JobOffer> offersToSave = new ArrayList<>();
                for (JobOfferDto jobofferDto : dtos) {
                    if (!jobOfferRepository.existsByExternalId(jobofferDto.getGuid())) {
                        offersToSave.add(mapper.mapToJobOffer(jobofferDto));
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
