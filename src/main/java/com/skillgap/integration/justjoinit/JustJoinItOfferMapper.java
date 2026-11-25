package com.skillgap.integration.justjoinit;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skillgap.integration.common.JobOfferDto;
import com.skillgap.integration.common.JobOfferDto.EmploymentTypeDto;

/**
 * Responsible for transforming raw DTO objects fetched from the JustJoin.it API 
 * into a unified data transfer model (JobOfferDto) used internally within the application.
 */

@Component
public class JustJoinItOfferMapper {

    public JobOfferDto toCommonDto(JustJoinOfferDto justJoinDto) {
        
        return new JobOfferDto(
            justJoinDto.guid(), 
            justJoinDto.title(),
            extractSkillNames(justJoinDto.requiredSkills()), 
            justJoinDto.workplaceType(), 
            justJoinDto.experienceLevel(), 
            justJoinDto.city(), 
            justJoinDto.publishedAt(), 
            extractEmploymentTypes(justJoinDto.employmentTypes())
        );
    }

    public List<JobOfferDto> toCommonDtoList(List<JustJoinOfferDto> dtos) {
        return dtos.stream()   
                            .map(this::toCommonDto)
                            .toList();
    }

    private List<EmploymentTypeDto> extractEmploymentTypes(List<JustJoinOfferDto.EmploymentType> employmentTypes) {
        if (employmentTypes == null || employmentTypes.isEmpty()) {
            return List.of();
        }

        return employmentTypes.stream()
                                .map(e -> new JobOfferDto.EmploymentTypeDto(e.from(), e.to(), e.currency()))
                                .toList();
    }

    private List<String> extractSkillNames(List<JustJoinOfferDto.Skill> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return List.of();
        }
        
        return requiredSkills.stream()
                                .map(JustJoinOfferDto.Skill::name)
                                .toList();
    }
}
