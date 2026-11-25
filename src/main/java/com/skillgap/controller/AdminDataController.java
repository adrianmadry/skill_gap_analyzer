package com.skillgap.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.skillgap.dto.external.CreateSkillRequestDto;
import com.skillgap.dto.external.TriggerImportRequestDto;
import com.skillgap.service.JobOfferImportService;
import com.skillgap.service.SkillExtractionService;
import com.skillgap.service.SkillService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Admin Operations", description = "Endpoints for administrative tasks, manual data ingestion triggers, and system monitoring")
public class AdminDataController {

    private final SkillExtractionService skillExtractionService;
    private final JobOfferImportService jobOfferImportService;
    private final SkillService skillService;

    @Operation(summary = "Get unmapped skills statistics", 
               description = "Returns a frequency map of unrecognized skill keywords extracted from job descriptions that are not yet present in the main skill dictionary")
    @GetMapping("/unknown-skills")
    public Map<String, Integer> getUnknownSkills () {
        return skillExtractionService.getUnknownSkillsCounter();
    }

    @Operation(summary = "Get available job offers providers", 
            description = "Returns a list of all supported job offers providers available for manual or automated data import"
    )
    @GetMapping("import/providers")
    public List<String> getAvalibleProviders() {
        return jobOfferImportService.getProvidersList();
    }

    @Operation(summary = "Trigger manual data import", 
           description = "Manually initiates the job offer import process for a specified provider without waiting for the scheduled CRON job")
    @PostMapping("import/trigger")
    public ResponseEntity<Void> triggerImportByProvider(
        @Valid @RequestBody TriggerImportRequestDto request
    ) {
        jobOfferImportService.importFromProviderByName(request.providerName());
        return ResponseEntity.accepted().build();
    }
    
}
