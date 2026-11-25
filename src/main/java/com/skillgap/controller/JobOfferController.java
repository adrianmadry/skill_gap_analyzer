package com.skillgap.controller;

import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillgap.dto.response.JobOfferResponseDto;
import com.skillgap.service.JobOfferService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Job Offers", description = "Endpoints for browsing and managing job offers")
public class JobOfferController {

    private final JobOfferService jobOfferService;

    @Operation(summary = "Get paginated job offers", 
               description = "Retrieves a paginated list of all imported job offers with optional sorting and pagination parameters")
    @GetMapping("/offers")
    public Page<JobOfferResponseDto> getAlloffers(@ParameterObject Pageable pageable) {
        return jobOfferService.getAllOffers(pageable);
    }

    @Operation(summary = "Get job offer details by ID", 
               description = "Returns full details of a single job offer identified by its unique database ID")
    @GetMapping("/offers/{id}")
    public ResponseEntity<JobOfferResponseDto> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(jobOfferService.getOfferById(id));
    }

    @Operation(summary = "Hard delete job offer by ID", 
               description = "Removes a job offer by its unique database ID")
    @DeleteMapping("/offers/{id}")
    public ResponseEntity<Void> deleteOfferById(@PathVariable Long id) {
        jobOfferService.deleteOfferById(id);
        return ResponseEntity.noContent().build();
    }


}
