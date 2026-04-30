package com.skillgap.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillgap.dto.response.JobOfferResponseDto;
import com.skillgap.service.JobOfferService;
import com.skillgap.service.SkillExtractionService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobOfferController {

    private final JobOfferService jobOfferService;
    private final SkillExtractionService skillExtractionService;

    @GetMapping("/offers")
    public Page<JobOfferResponseDto> getAlloffers(Pageable pageable) {
        return jobOfferService.getAllOffers(pageable);
    }

    @GetMapping("/stats/unknown-skills")
    public Map<String, Integer> getUnknownSkills () {
        return skillExtractionService.getUnknownSkillsCounter();
    }
    

}
