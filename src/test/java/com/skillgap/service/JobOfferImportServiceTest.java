package com.skillgap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import com.skillgap.dao.ImportLogRepository;
import com.skillgap.dao.JobOfferRepository;
import com.skillgap.entity.JobOffer;
import com.skillgap.integration.common.JobOfferDto;
import com.skillgap.service.provider.JobOffersProvider;

@ExtendWith(MockitoExtension.class)
public class JobOfferImportServiceTest {

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private ImportLogRepository importLogRepository;

    @Mock
    private JobOfferService jobOfferService;

    private JobOfferImportService jobOfferImportService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Captor
    private ArgumentCaptor<List<JobOffer>> offersCaptor;


    @Nested
    @DisplayName("Tests for importAll() method with single provider as test input")
    class SingleProviderTests {

        @Mock
        private JobOffersProvider jobOffersProvider;

        @BeforeEach
        void setUp() {
            // Manual initialization to inject mock JobOffersProvider list
            jobOfferImportService = new JobOfferImportService(
                List.of(jobOffersProvider), 
                jobOfferService,
                jobOfferRepository, 
                transactionTemplate,
                importLogRepository
            );
        }

        @Test
        @DisplayName("Should not import offers that already exist in the database")
        void shouldAvoidImportingExistingOffers() {
            // Given
            String existingGuid = "offer1";
            when(jobOfferRepository.findAllExternalIds()).thenReturn(Set.of(existingGuid));

            JobOfferDto existingOfferDto = new JobOfferDto();
            existingOfferDto.setGuid(existingGuid);
            when(jobOffersProvider.fetchAll()).thenReturn(List.of(existingOfferDto));

            // When
            jobOfferImportService.importAll();

            // Then
            // 1. Check if the service did not attempt to map the offer
            verify(jobOfferService, never()).mapFromDto(any());
            
            // 2. Verify that an empty list was passed to the repository's saveAll method
            verify(jobOfferRepository).saveAll(offersCaptor.capture());
            assertThat(offersCaptor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("Should save only offers processed by jobOfferService")
        void shouldSaveProcessedOffers() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            dto.setGuid("af342432");

            // Entity for mapping service
            JobOffer entity = new JobOffer();
            entity.setExternalId("af342432");

            when(jobOfferRepository.findAllExternalIds()).thenReturn(new HashSet<>());
            when(jobOffersProvider.fetchAll()).thenReturn(List.of(dto));
            when(jobOfferService.mapFromDto(dto)).thenReturn(entity);
            
            // When
            jobOfferImportService.importAll();

            // Then
            verify(jobOfferService).mapFromDto(dto);

            verify(jobOfferRepository).saveAll(offersCaptor.capture());
            assertThat(offersCaptor.getValue()).containsExactlyInAnyOrder(entity);
        }
    }


    @Nested
    @DisplayName("Tests for importAll() method with multiple providers as test input")
    class MultipleProviderTests {

        @Mock
        private JobOffersProvider jobOffersProvider1;

        @Mock
        private JobOffersProvider jobOffersProvider2;

        @BeforeEach
        void setUp() {
            // Manual initialization to inject mock JobOffersProvider list
            jobOfferImportService = new JobOfferImportService(
                List.of(jobOffersProvider1, jobOffersProvider2), 
                jobOfferService,
                jobOfferRepository, 
                transactionTemplate,
                importLogRepository
            );
        }

        @Test
        @DisplayName("Should import data from all providers from provider's list")
        void shouldImportDataFromAllProviders() {
            // Given
            when(jobOffersProvider1.fetchAll()).thenReturn(List.of());
            when(jobOffersProvider2.fetchAll()).thenReturn(List.of());

            // When
            jobOfferImportService.importAll();

            // Then
            verify(jobOffersProvider1).fetchAll();
            verify(jobOffersProvider2).fetchAll();

        }

        @Test
        @DisplayName("Should continue processing next providers if one throws an exception")
        void shouldContinueWhenProviderFails() {
            // Given
            when(jobOffersProvider1.fetchAll()).thenThrow(new RuntimeException("API Connection Error"));
            when(jobOffersProvider2.fetchAll()).thenReturn(List.of());

            when(jobOfferRepository.findAllExternalIds()).thenReturn(Set.of());

            // When
            jobOfferImportService.importAll();

            // Then
            // Verify that fetchAll() method was called for both providers
            verify(jobOffersProvider1).fetchAll();
            verify(jobOffersProvider2).fetchAll();

            // Verify that saveAll() was still executed for the successful provider
            verify(jobOfferRepository).saveAll(any());
        }

        @Test
        @DisplayName("Should avoid repetition of save offer duplicated in different providers")
        void shouldHandleDuplictedOffersBetweenProviders() {
            // Given
            String sharedExternalId = "shared-offer1";

            JobOfferDto dtoFromP1 = new JobOfferDto();
            dtoFromP1.setGuid(sharedExternalId);

            JobOfferDto dtoFromP2 = new JobOfferDto();
            dtoFromP2.setGuid(sharedExternalId);

            JobOffer entity = new JobOffer();
            entity.setExternalId(sharedExternalId);

            when(jobOfferRepository.findAllExternalIds()).thenReturn(new HashSet<>());

            when(jobOffersProvider1.fetchAll()).thenReturn(List.of(dtoFromP1));
            when(jobOffersProvider2.fetchAll()).thenReturn(List.of(dtoFromP2));

            when(jobOfferService.mapFromDto(any())).thenReturn(entity);

            // When
            jobOfferImportService.importAll();

            // Then
            // Verify that mapping was performed only once despite two sources
            verify(jobOfferService, times(1)).mapFromDto(any());
            
            // Capture all calls to saveAll to verify the total count of saved objects
            verify(jobOfferRepository, times(2)).saveAll(offersCaptor.capture());
    
            int totalSavedOffers = offersCaptor.getAllValues().stream()
                                                .mapToInt(List::size)
                                                .sum();
            
            assertThat(totalSavedOffers)
                .as("Total number of saved offers should be 1 to avoid duplicates between providers")
                .isEqualTo(1);

        }

    }
}
