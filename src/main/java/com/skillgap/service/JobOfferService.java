package com.skillgap.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.dto.response.JobOfferResponseDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.Skill;
import com.skillgap.mapper.ApiJobOfferMapper;
import com.skillgap.mapper.ExternalJobOfferMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final ApiJobOfferMapper apiJobOfferMapper;
    private final SkillService skillService;
    private final SkillExtractionService skillExtractionService;
    private final ExternalJobOfferMapper externalJobOfferMapper;

    public Page<JobOfferResponseDto> getAllOffers(Pageable pageable) {
        return jobOfferRepository.findAll(pageable)
                                    .map(offer -> apiJobOfferMapper.mapFromEntityToDto(offer));         
    }

    public JobOffer mapFromDto(JobOfferDto jobOfferDto) {
        Set<String> skillNamesFromDict = skillExtractionService.extractRequiredSkills(jobOfferDto);
        Set<Skill> skills = skillService.getFromDbOrCreate(skillNamesFromDict);

        return externalJobOfferMapper.mapToJobOffer(jobOfferDto, skills); 
    }




}
