package com.skillgap.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.skillgap.dao.ImportLogRepository;
import com.skillgap.dao.JobOfferRepository;
import com.skillgap.entity.ImportLog;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.enums.ImportStatus;
import com.skillgap.integration.common.JobOfferDto;
import com.skillgap.service.provider.JobOffersProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobOfferImportService {

    private final List<JobOffersProvider> providers;
    private final JobOfferService jobOfferService;
    private final JobOfferRepository jobOfferRepository;
    private final TransactionTemplate transactionTemplate;
    private final ImportLogRepository importLogRepository;

    public void importAll() {
        log.info("Starting job offer import from {} providers", providers.size());

        Set<String> existingIdInDb = jobOfferRepository.findAllExternalIds();

        for (JobOffersProvider provider : providers) {
            try {
                importFromProvider(provider, existingIdInDb);

            } catch (Exception e) {
                log.error("Failed to import offers from provider: {}. Error: {}", provider.getClass().getSimpleName(), e.getMessage());
            }
        }
        log.info("Job offer import process finished.");
    }

    private ImportLog importFromProvider(JobOffersProvider provider, Set<String> existingIdInDb) {

        String providerName = provider.getClass().getSimpleName();

        ImportLog importLog = initializeImportLog(providerName);
        log.info("Fetching offers from provider: {}", providerName);

        try {
            List<JobOfferDto> fetchedDtos = provider.fetchAll();

            List<JobOfferDto> uniqueFetchedDtos = deduplicateBatch(fetchedDtos);
            List<JobOfferDto> filteredDtos = filterNewBatch(uniqueFetchedDtos, existingIdInDb);
            
            List<JobOffer> offersToSave = new ArrayList<>();

            for (JobOfferDto jobOfferDto : filteredDtos) {
                    JobOffer entity = jobOfferService.mapFromDto(jobOfferDto);
                    offersToSave.add(entity);
            }
            
            finalizeSuccessImportLog(importLog, fetchedDtos.size(), offersToSave.size());

            transactionTemplate.executeWithoutResult(status -> {
                jobOfferRepository.saveAll(offersToSave);
                importLogRepository.save(importLog);
                filteredDtos.forEach(dto -> existingIdInDb.add(dto.getGuid()));
            });
            
            

        } catch (Exception e) {
            log.error("Error occurred while importing from provider: {}", providerName, e);

            finalizeFailedImportLog(e, importLog);
            importLogRepository.save(importLog);

        }

        return importLog;

    }

    // Removes duplicate job offer DTOs within the fetched batch based on GUID.
    private List<JobOfferDto> deduplicateBatch(List<JobOfferDto> dtosBatch) {
        return dtosBatch.stream()
                        .filter(dto -> dto.getGuid() != null)
                        .collect(Collectors.toMap(
                            JobOfferDto::getGuid,
                            dto -> dto,
                            (existing, duplicate) -> {
                                log.warn("Duplicate guid found in provider response: {}", existing.getGuid());
                                return existing;
                            },
                            LinkedHashMap::new
                        ))
                        .values()
                        .stream()
                        .toList();
    }

    private void finalizeFailedImportLog(Exception e, ImportLog importLog) {
        String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        importLog.setEndTime(LocalDateTime.now());
        importLog.setStatus(ImportStatus.FAILED);
        importLog.setErrorMessage(errorMessage);
    }

    private void finalizeSuccessImportLog(ImportLog importLog, int fetchedCount, int savedCount) {
            int skippedCount = fetchedCount - savedCount;

            importLog.setFetchedCount(fetchedCount);
            importLog.setSavedCount(savedCount);
            importLog.setSkippedCount(skippedCount);
            importLog.setEndTime(LocalDateTime.now());
            importLog.setStatus(ImportStatus.SUCCESS);

            log.info("Provider: {} | fetched {} | saved to database {} | skipped (duplicates): {}", 
                importLog.getProviderName(), fetchedCount, savedCount, skippedCount);
    }

    // Filters out job offer DTOs that already exist in the database.
    private List<JobOfferDto> filterNewBatch(List<JobOfferDto> dtos, Set<String> existingIdInDb) {
        return dtos.stream()
                    .filter(dto -> {
                        boolean isExisting = existingIdInDb.contains(dto.getGuid());
                        if (isExisting) {
                            log.warn("Duplicate offer found in database, skipping. GUID: {}", dto.getGuid());
                        }
                        return !isExisting;
                    })
                    .toList();
    }

    private ImportLog initializeImportLog(String providerName) {
        return ImportLog.builder()
                        .providerName(providerName)
                        .startTime(LocalDateTime.now())
                        .build();
    }

}