package com.skillgap.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skillgap.dto.response.JobOfferResponseDto;
import com.skillgap.entity.JobOffer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor 
public class ApiJobOfferMapper {

    public JobOfferResponseDto mapFromEntityToDto(JobOffer entity) {
        if (entity == null) return null;

        JobOfferResponseDto dto = new JobOfferResponseDto();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCompanyName(entity.getCompanyName());
        dto.setCity(entity.getCity());
        dto.setCountry(entity.getCountry());
        dto.setDescription(entity.getDescription());
        dto.setSalaryMin(entity.getSalaryMin());
        dto.setSalaryMax(entity.getSalaryMax());
        dto.setCurrency(entity.getCurrency());
        dto.setPublishedDate(entity.getPublishedDate());

        // Enums mapping
        if (entity.getExperienceLevel() != null) {
            dto.setExperienceLevel(entity.getExperienceLevel().name());
        }
        if (entity.getWorkModel() != null) {
            dto.setWorkModel(entity.getWorkModel().name());
        }
        if (entity.getOfferSource() != null) {
            dto.setOfferSource(entity.getOfferSource().name());
        }
        

        if (entity.getSkills() != null) {
            dto.setSkills(entity.getSkills().stream()
                                        .map(skill -> skill.getName())
                                        .collect(Collectors.toSet()));
        }
        
        return dto;
    }

}
