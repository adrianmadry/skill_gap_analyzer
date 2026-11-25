package com.skillgap.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.skillgap.dto.response.JobOfferResponseDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.ExperienceLevel;
import com.skillgap.entity.enums.OfferSource;
import com.skillgap.entity.enums.WorkModel;

public class ApiJobOfferMapperTest {

    private final ApiJobOfferMapper apiJobOfferMapper = new ApiJobOfferMapper();

    @Nested
    @DisplayName("Test for mapFromEntityToDto method")
    class MapFromEntityToDtoTests {

        @Test
        @DisplayName("Should return JobOfferResponseDto object with proper data mappings")
        void shouldMapToResponseDto() {
            // Given
            JobOffer jobOffer = new JobOffer();
            jobOffer.setId(243243252L);
            jobOffer.setTitle("developer");
            jobOffer.setSalaryMax(BigDecimal.valueOf(12000.00));
            jobOffer.setPublishedDate(LocalDate.of(2025, 12, 5));

            // When
            JobOfferResponseDto result = apiJobOfferMapper.mapFromEntityToDto(jobOffer);

            // Then
            assertThat(result.getId()).isEqualTo(jobOffer.getId());
            assertThat(result.getTitle()).isEqualTo(jobOffer.getTitle());
            assertThat(result.getSalaryMax()).isEqualByComparingTo(jobOffer.getSalaryMax());
            assertThat(result.getPublishedDate()).isEqualTo(jobOffer.getPublishedDate());
        }

        @Test
        @DisplayName("Should return null object when JobOffer argument is null")
        void shouldHandleNullArg() {
            // Given
            JobOffer jobOffer = null;

            // When
            JobOfferResponseDto result = apiJobOfferMapper.mapFromEntityToDto(jobOffer);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should map Enums from JobOffer to Strings in Response Dto")
        void shouldMapEnums() {
            // Given
            JobOffer jobOffer = new JobOffer();
            jobOffer.setExperienceLevel(ExperienceLevel.MID);
            jobOffer.setWorkModel(WorkModel.STATIONARY);
            jobOffer.setOfferSource(OfferSource.ROCKETJOBS);

            // When
            JobOfferResponseDto result = apiJobOfferMapper.mapFromEntityToDto(jobOffer);

            // Then
            assertThat(result.getExperienceLevel()).isEqualTo(jobOffer.getExperienceLevel().name());
            assertThat(result.getWorkModel()).isEqualTo(jobOffer.getWorkModel().name());
            assertThat(result.getOfferSource()).isEqualTo(jobOffer.getOfferSource().name());
        }

        @Test
        @DisplayName("Should map a set of Skill entities to a set of skill names in the Response Dto")
        void shouldMapSkillsSet() {
            // Given
            JobOffer jobOffer = new JobOffer();
            Set<Skill> skills = new HashSet<>();
            Skill skill1 = new Skill("java");
            Skill skill2 = new Skill("python");
            skills.add(skill1);
            skills.add(skill2);
            
            jobOffer.setSkills(skills);

            // When
            JobOfferResponseDto result = apiJobOfferMapper.mapFromEntityToDto(jobOffer);

            // Then
            assertThat(result.getSkills())
                                    .hasSize(2)
                                    .containsExactlyInAnyOrder(skill1.getName(), skill2.getName());
        }

        @Test
        @DisplayName("Should set null value to skills field if Skill Set is null")
        void shouldHandleNullSkillsSet() {
            // Given
            JobOffer jobOffer = new JobOffer();
            jobOffer.setSkills(null);

            // When
            JobOfferResponseDto result = apiJobOfferMapper.mapFromEntityToDto(jobOffer);

            // Then
            assertThat(result.getSkills()).isNull();
                                    
        }

    }

}

