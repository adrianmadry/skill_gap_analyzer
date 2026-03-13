package com.skillgap.service.provider;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Component;

import com.skillgap.dto.external.JobOfferDto;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SnapshotJobOffersProvider implements JobOffersProvider {

    private final ObjectMapper objectMapper;

    @Override
    public List<JobOfferDto> fetchAll() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/offers.json");
            return objectMapper.readValue(inputStream, 
                                        new TypeReference<List<JobOfferDto>>() {});                                   
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }

    @Override
    public List<JobOfferDto> fetchByCity(String city) {
        return this.fetchAll().stream()
                                .filter(dto -> dto.getCity().equalsIgnoreCase(city))
                                .toList();


    }
}
