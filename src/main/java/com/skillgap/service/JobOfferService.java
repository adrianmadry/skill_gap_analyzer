package com.skillgap.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dao.JobOfferRepository.RoleCountProjection;
import com.skillgap.dto.response.JobOfferResponseDto;
import com.skillgap.dto.response.RoleDistributionDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.Skill;
import com.skillgap.exception.JobOfferNotFoundException;
import com.skillgap.exception.ResourceNotFoundException;
import com.skillgap.integration.common.JobOfferDto;
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

    public JobOfferResponseDto getOfferById(Long id) {
        return jobOfferRepository.findById(id)
                                .map(apiJobOfferMapper::mapFromEntityToDto)
                                .orElseThrow(() -> new ResourceNotFoundException("Job offer with ID: " + id + " not found"));
    }

    public List<RoleDistributionDto> getRolesDistribution(String city) {
        List<RoleCountProjection> roleCounts = jobOfferRepository.getRolesDistribution(city);

        long totalOffers = roleCounts.stream()
                                    .mapToLong(RoleCountProjection::getCount)
                                    .sum();

        return roleCounts.stream()
                    .map(roleProjection -> new RoleDistributionDto(
                                                            roleProjection.getRoleName(), 
                                                            roleProjection.getCount(), 
                                                            Math.round((double) roleProjection.getCount() / totalOffers * 100 * 100.0) / 100.0
                    ))
                    .toList();
    }

    @Transactional 
    public void deleteOfferById(Long id) {
        if (!jobOfferRepository.existsById(id)) {
            throw new JobOfferNotFoundException(id);
        }
        jobOfferRepository.deleteById(id);
    }


}
