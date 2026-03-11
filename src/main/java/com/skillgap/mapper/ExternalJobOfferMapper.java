package com.skillgap.mapper;

import java.time.ZoneId;

import org.springframework.stereotype.Service;

import com.skillgap.dao.SkillRepository;
import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.ExperienceLevel;
import com.skillgap.entity.enums.WorkModel;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service //TODO
@RequiredArgsConstructor //TODO
public class ExternalJobOfferMapper {

    private final SkillRepository skillRepository;

    @Transactional
    public JobOffer mapToJobOffer(JobOfferDto jobOfferDto) {

        JobOffer offer = new JobOffer();
        offer.setTitle(jobOfferDto.getTitle());
        offer.setExternalId(jobOfferDto.getGuid());
        offer.setWorkModel(WorkModel.fromString(jobOfferDto.getWorkplaceType()));
        offer.setExperienceLevel(ExperienceLevel.fromString(jobOfferDto.getExperienceLevel()));
        offer.setCity(jobOfferDto.getCity());
        offer.setPublishedDate(jobOfferDto.getPublishedAt().atZone(ZoneId.systemDefault()).toLocalDate());

        mapRequiredSkills(offer, jobOfferDto);

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

    public void mapRequiredSkills(JobOffer jobOffer, JobOfferDto externalOfferDto) {
        if (externalOfferDto.getRequiredSkills() == null) {
            return;
        }

        for (String skillName : externalOfferDto.getRequiredSkills()) {
            if (skillName == null || skillName.isBlank()) {
                continue;
            }

            // Check if skill already exists in db
            Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                        .orElseGet(() -> {
                            Skill s = new Skill();
                            s.setName(skillName);
                            return skillRepository.save(s);
                        });

            // Assocciate Skill to JobOffer
            jobOffer.addSkill(skill);
        }
    }


           

    



}
