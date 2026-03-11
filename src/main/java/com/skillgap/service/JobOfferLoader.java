package com.skillgap.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.mapper.ExternalJobOfferMapper;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class JobOfferLoader {
    
    private final JobOfferRepository jobOfferRepository;
    private final ExternalJobOfferMapper jobOfferMapper;
    private final ObjectMapper objectMapper;

    public void loadInitialData() {

        if (jobOfferRepository.count() > 0) {
            System.out.println("DataBase is not empty, initial data import canceled");
            return;
        }

        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/offers.json");

            List<JobOfferDto> dtos = objectMapper.readValue(inputStream, 
                                                            new TypeReference<List<JobOfferDto>>() {}); 

            List<JobOffer> offersToSave = new ArrayList<>();
            for (JobOfferDto jobofferDto : dtos) {
                offersToSave.add(jobOfferMapper.mapToJobOffer(jobofferDto));
            }
            jobOfferRepository.saveAll(offersToSave);

            System.out.println("Initial data loaded to database");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load startup data", e);
        }
    }

}
