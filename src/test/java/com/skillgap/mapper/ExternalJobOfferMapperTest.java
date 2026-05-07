package com.skillgap.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.dto.external.JobOfferDto.EmploymentTypeDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.ExperienceLevel;
import com.skillgap.entity.enums.WorkModel;
import com.skillgap.service.JobRoleExtractor;

@ExtendWith(MockitoExtension.class)
public class ExternalJobOfferMapperTest {
    
    @Nested
    @DisplayName("Test for mapToJobOffer method")
    class mapToJobOfferTests {

        @Mock
        private JobRoleExtractor jobRoleExtractor;

        private ExternalJobOfferMapper externalJobOfferMapper;

        @BeforeEach
        void setUp() {
            externalJobOfferMapper = new ExternalJobOfferMapper(jobRoleExtractor);
        }
        
        @Test
        @DisplayName("Should return JobOffer entity with all data from DTO")
        void shouldMapToJobOffer() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            dto.setGuid("guid324234");
            dto.setExperienceLevel("mid");
            dto.setCity("Krakow");
            dto.setTitle("backend developer");

            // When
            JobOffer result = externalJobOfferMapper.mapToJobOffer(dto, Set.of());

            // Then
            assertThat(result.getExternalId()).isEqualTo(dto.getGuid());
            assertThat(result.getExperienceLevel().name()).isEqualToIgnoringCase(dto.getExperienceLevel());
            assertThat(result.getCity()).isEqualTo(dto.getCity());
            assertThat(result.getTitle()).isEqualTo(dto.getTitle());
        }   

        @Test
        @DisplayName("Should convert published date to LocalDate")
        void shouldConvertPublishedDate() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            dto.setPublishedAt(Instant.parse("2023-10-27T10:15:30Z"));

            // When
            JobOffer result = externalJobOfferMapper.mapToJobOffer(dto, Set.of());

            // Then
            assertThat(result.getPublishedDate()).isEqualTo(LocalDate.of(2023, 10, 27));
        }

        @Test
        @DisplayName("Should map work model and experience level using enum methods")
        void shouldMapEnums() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            dto.setWorkplaceType("remote");
            dto.setExperienceLevel("junior");

            // When
            JobOffer result = externalJobOfferMapper.mapToJobOffer(dto, Set.of());

            // Then
            assertThat(result.getWorkModel()).isEqualTo(WorkModel.REMOTE);
            assertThat(result.getExperienceLevel()).isEqualTo(ExperienceLevel.JUNIOR);
        }

        @Test
        @DisplayName("Should return JobOffer with associated skills from given Set of skills")
        void shouldAssociateSkills() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            Skill skill1 = new Skill();
            Skill skill2 = new Skill();
            skill1.setName("skill 1");
            skill2.setName("skill 2");
            Set<Skill> skills = Set.of(skill1, skill2);

            // When
            JobOffer result = externalJobOfferMapper.mapToJobOffer(dto, skills);

            // Then
            assertThat(result.getSkills())
                                .hasSize(2)
                                .containsExactlyInAnyOrder(skill1, skill2);
        }

        @Test
        @DisplayName("Should map payment data and normalize currency to uppercase")
        void shouldMapPaymentData() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            var salaryMin = BigDecimal.valueOf(5000.00);
            var salaryMax = BigDecimal.valueOf(15000.00);
            var currency = "usd";
            EmploymentTypeDto empType = new EmploymentTypeDto(salaryMin, salaryMax, currency);
            dto.setEmploymentTypes(List.of(empType));

            // When
            JobOffer result = externalJobOfferMapper.mapToJobOffer(dto, Set.of());

            // Then
            assertThat(result.getSalaryMin()).isEqualByComparingTo(salaryMin);
            assertThat(result.getSalaryMax()).isEqualByComparingTo(salaryMax);
            assertThat(result.getCurrency()).isEqualTo(currency.toUpperCase());
        }

        @Test
        @DisplayName("Should leave salary fields null when employment types list is empty or null")
        void shouldHandleNoPaymentData() {
            // Given
                // Case 1 - empty list
            JobOfferDto dtoWEmptyList = new JobOfferDto();
            dtoWEmptyList.setEmploymentTypes(List.of());
                // Case 2 - null list
            JobOfferDto dtoWNullList = new JobOfferDto();
            dtoWNullList.setEmploymentTypes(null);
            
            // When
            JobOffer result1 = externalJobOfferMapper.mapToJobOffer(dtoWEmptyList, Set.of());
            JobOffer result2 = externalJobOfferMapper.mapToJobOffer(dtoWNullList, Set.of());

            // Then
            assertThat(result1.getCurrency()).isNull();
            assertThat(result1.getSalaryMin()).isNull();
            assertThat(result1.getSalaryMax()).isNull();

            assertThat(result2.getCurrency()).isNull();
            assertThat(result2.getSalaryMin()).isNull();
            assertThat(result2.getSalaryMax()).isNull();

        }

    }

}

