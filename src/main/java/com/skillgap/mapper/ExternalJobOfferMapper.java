package com.skillgap.mapper;

import java.time.ZoneId;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.ExperienceLevel;
import com.skillgap.entity.enums.WorkModel;
import com.skillgap.service.JobRoleExtractor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExternalJobOfferMapper {

    private final JobRoleExtractor jobRoleExtractor;

    public JobOffer mapToJobOffer(JobOfferDto jobOfferDto, Set<Skill> skills) {
        JobOffer offer = new JobOffer();
        offer.setTitle(jobOfferDto.getTitle());
        offer.setExternalId(jobOfferDto.getGuid());
        offer.setWorkModel(WorkModel.fromString(jobOfferDto.getWorkplaceType()));
        offer.setExperienceLevel(ExperienceLevel.fromString(jobOfferDto.getExperienceLevel()));
        offer.setCity(jobOfferDto.getCity());
        offer.setRoleTag(jobRoleExtractor.matchRole(jobOfferDto.getTitle()));
        if (jobOfferDto.getPublishedAt() != null) {
            offer.setPublishedDate(jobOfferDto.getPublishedAt().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        // Assocciate Skills to JobOffer
        for (Skill skill: skills) {
            offer.addSkill(skill);
        }
        
        mapPaymentData(offer, jobOfferDto);

        return offer;
    }

    private void mapPaymentData(JobOffer offer, JobOfferDto jobOfferDto) {
        var employmentTypes = jobOfferDto.getEmploymentTypes();

        if (employmentTypes != null && !employmentTypes.isEmpty()) {
            var mainType = employmentTypes.get(0);
            offer.setSalaryMin(mainType.getFrom());
            offer.setSalaryMax(mainType.getTo());

            if(mainType.getCurrency() != null) {
                offer.setCurrency(mainType.getCurrency().toUpperCase());
            }
        }
    }

}
