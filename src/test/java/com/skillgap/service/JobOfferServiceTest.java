package com.skillgap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dto.external.JobOfferDto;
import com.skillgap.dto.response.JobOfferResponseDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.Skill;
import com.skillgap.mapper.ApiJobOfferMapper;
import com.skillgap.mapper.ExternalJobOfferMapper;

@ExtendWith(MockitoExtension.class)
public class JobOfferServiceTest {

    @InjectMocks
    private JobOfferService jobOfferService;

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private ApiJobOfferMapper apiJobOfferMapper;

    @Mock
    private SkillService skillService;
    
    @Mock
    private SkillExtractionService skillExtractionService;
    
    @Mock
    private ExternalJobOfferMapper externalJobOfferMapper;

    @Captor
    private ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @Captor
    private ArgumentCaptor<Set<String>> skillNamesCaptor;

    @Captor 
    private ArgumentCaptor<JobOfferDto> jobOfferDtoCaptor;
    
    @Captor 
    private ArgumentCaptor<Set<Skill>> skillsCaptor;

    @Nested
    @DisplayName("Tests for getAllOffers() method")
    class GetAllOffersTests {

        @Test
        @DisplayName("Should pass Pageable object to jobOfferRepository searching method")
        void shouldPassPageableArgToRepository() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            when(jobOfferRepository.findAll(pageable)).thenReturn(Page.empty());

            // When
            jobOfferService.getAllOffers(pageable);

            // Then
            verify(jobOfferRepository).findAll(pageableArgumentCaptor.capture());
            assertThat(pageableArgumentCaptor.getValue()).isEqualTo(pageable);
        }

        @Test
        @DisplayName("Should map every JobOffer entity to JobOfferResponseDto")
        void shouldMapJobOfferEntity() {
            // Given
            JobOffer offer1 = new JobOffer();
            offer1.setExternalId("23432");
            JobOffer offer2 = new JobOffer();
            offer2.setExternalId("2fdf32");
            List<JobOffer> offers = List.of(offer1, offer2);

            // Mock repository behaviour
            Pageable pageable = PageRequest.of(0, 10);
            Page<JobOffer> jobOfferPage = new PageImpl<>(offers, pageable, offers.size());
            when(jobOfferRepository.findAll(pageable)).thenReturn(jobOfferPage);

            // Mock mapper behaviour
            when(apiJobOfferMapper.mapFromEntityToDto(any(JobOffer.class))).thenReturn(new JobOfferResponseDto());

            // When
            Page<JobOfferResponseDto> result = jobOfferService.getAllOffers(pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            verify(apiJobOfferMapper, times(2)).mapFromEntityToDto(any());
        }
    }

    @Nested
    @DisplayName("Tests for mapFromDto() method")
    class MapFromDtoTests {

        private JobOfferDto jobOfferDto = new JobOfferDto();

        @Test
        @DisplayName("Should pass extracted skill names to Skill Service as argument")
        void shouldPassSkillNamesToSkillService() {
            // Given
            String skill1 = "skill1";
            String skill2 = "skill2";

            when(skillExtractionService.extractRequiredSkills(jobOfferDto)).thenReturn(Set.of(skill1, skill2));
            when(skillService.getFromDbOrCreate(anySet())).thenReturn(Set.of());

            // When
            jobOfferService.mapFromDto(jobOfferDto);

            // Then
            verify(skillService).getFromDbOrCreate(skillNamesCaptor.capture());
            assertThat(skillNamesCaptor.getValue()).containsExactlyInAnyOrder(skill1, skill2);

        }

        @Test
        @DisplayName("Should pass both DTO and fetched skill entities to the external mapper")
        void shouldPassDtoAndSkillsToMapper() {
            // Given
            Skill skill1 = new Skill("skill1");
            Skill skill2 = new Skill("skill2");

            when(skillExtractionService.extractRequiredSkills(jobOfferDto)).thenReturn(Set.of());
            when(skillService.getFromDbOrCreate(anySet())).thenReturn(Set.of(skill1, skill2));
            when(externalJobOfferMapper.mapToJobOffer(any(), anySet())).thenReturn(new JobOffer());

            // When
            jobOfferService.mapFromDto(jobOfferDto);

            // Then
            verify(externalJobOfferMapper).mapToJobOffer(jobOfferDtoCaptor.capture(), skillsCaptor.capture());
            assertThat(jobOfferDtoCaptor.getValue()).isEqualTo(jobOfferDto);
            assertThat(skillsCaptor.getValue()).containsExactlyInAnyOrder(skill1, skill2);
        }

    }



}
