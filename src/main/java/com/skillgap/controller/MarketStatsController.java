package com.skillgap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillgap.dto.response.RoleDistributionDto;
import com.skillgap.dto.response.SkillStatsDto;
import com.skillgap.entity.enums.JobRoleTag;
import com.skillgap.service.JobOfferService;
import com.skillgap.service.SkillAnalysisService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/market-stats")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Market Statistics", description = "Endpoints for aggregated job market statistics and technology trends")
public class MarketStatsController {

    private final SkillAnalysisService skillAnalysisService;
    private final JobOfferService jobOfferService;

    @Operation(summary = "Get top skills by popularity", 
               description = "Returns a list of the most popular skills, with optional filtering by city and job role")
    @GetMapping("/skills/top")
    public List<SkillStatsDto> getTopSkills(
                    @RequestParam(required = false) String city,
                    @RequestParam(required = false) JobRoleTag role,
                    @RequestParam(defaultValue = "10") int limit
    ) {
        return skillAnalysisService.getTopSkills(city, role, limit);
    }

    @Operation(summary = "Get historical technology trends", 
           description = "Returns time-series data showing the popularity changes of specific skills over time")
    @GetMapping("skills/trends")
    public void getMarketTrends() {
        return;
    }

    @Operation(summary = "Get job roles percentage distribution", 
           description = "Returns the percentage share of each job role relative to the total number of offers in the system")
    @GetMapping("/roles/distribution")
    public  List<RoleDistributionDto> getRolesDistribution(
        @RequestParam(required = false) String city
    ) {
        return jobOfferService.getRolesDistribution(city);
    }

}
