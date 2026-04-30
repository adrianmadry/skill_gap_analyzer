package com.skillgap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.skillgap.service.JobOfferService;
import com.skillgap.service.SkillExtractionService;

@WebMvcTest(JobOfferController.class)
@ActiveProfiles("test")
public class JobOfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobOfferService jobOfferService;
    
    @MockitoBean
    private SkillExtractionService skillExtractionService;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Tests for getAllOffers() method")
    class getAllOffersTests {

        @Test
        @DisplayName("Should return 200 OK and pass correct pagination parameters to service")
        void shouldPassPaginationParamsToService() throws Exception {
            // Given
            int pageNumber = 1;
            int pageSize = 5;

            when(jobOfferService.getAllOffers(any(Pageable.class))).thenReturn(Page.empty());

            // When & Then
            mockMvc.perform(get("/api/offers")
                            .param("page", String.valueOf(pageNumber))
                            .param("size", String.valueOf(pageSize)))
                    .andExpect(status().isOk()); // 200 status code verification

            verify(jobOfferService).getAllOffers(pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(pageNumber);
            assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(pageSize);
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error when service throws an exception")
        void shouldReturn500WhenServiceThrowsException() throws Exception {
            // Given
            when(jobOfferService.getAllOffers(any(Pageable.class)))
                                            .thenThrow(new RuntimeException("Database conn failed"));

            // When & Then
            mockMvc.perform(get("/api/offers")).andExpect(status().isInternalServerError());
            verify(jobOfferService).getAllOffers(any(Pageable.class));
        }

        @Test
        @DisplayName("Should use default pagination when no params provided")
        void shouldUseDefaultPagination() throws Exception {
            when(jobOfferService.getAllOffers(any(Pageable.class))).thenReturn(Page.empty());

            mockMvc.perform(get("/api/offers")).andExpect(status().isOk()); 
            
            verify(jobOfferService).getAllOffers(pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0);
            assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);

        }
    }

    @Nested
    @DisplayName("Tests for getUnknownSkills() method")
    class getUnknownSkillsTests {
        @Test
        @DisplayName("Should return unknown skills statistics and correct JSON structure")
        void shouldReturnUnknownSkills() throws Exception {
            // Given
            Map<String, Integer> unknownSkills = new LinkedHashMap<>();
            unknownSkills.put("java", 24);
            unknownSkills.put("python", 11);
            when(skillExtractionService.getUnknownSkillsCounter()).thenReturn(unknownSkills);

            // When && Then
            mockMvc.perform(get("/api/stats/unknown-skills"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.java").value(24))
                                .andExpect(jsonPath("$.python").value(11))
                                .andExpect(jsonPath("$.length()").value(2));
                                
            verify(skillExtractionService, times(1)).getUnknownSkillsCounter();
        }

        @Test
        @DisplayName("Should return empty Map if that was output from service")
        void shouldHandleEmptyMapFromService() throws Exception {
            // Given
            when(skillExtractionService.getUnknownSkillsCounter()).thenReturn(new LinkedHashMap<>());

            // When & Then
            mockMvc.perform(get("/api/stats/unknown-skills"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
                                
            verify(skillExtractionService).getUnknownSkillsCounter();
        }

    }
}
