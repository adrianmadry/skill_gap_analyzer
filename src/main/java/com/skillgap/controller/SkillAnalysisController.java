package com.skillgap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillgap.dto.external.SkillGapRequestDto;
import com.skillgap.dto.response.CoOccurrenceResultDto;
import com.skillgap.dto.response.SkillGapResponseDto;
import com.skillgap.dto.response.SkillStatsDto;
import com.skillgap.entity.enums.JobRoleTag;
import com.skillgap.service.SkillAnalysisService;
import com.skillgap.service.SkillGapService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Skills Analysis", description = "Endpoints to inspect correlactions between technologies/skills")
public class SkillAnalysisController {

    private final SkillAnalysisService skillAnalysisService;
    private final SkillGapService skillGapService;

    @Operation(summary = "Get top skills by popularity", 
               description = "Returns a list of the most popular skills, with optional filtering by city and job role")
    @GetMapping("/top-skills")
    public List<SkillStatsDto> getTopSkills(
                    @RequestParam(required = false) String city,
                    @RequestParam(required = false) JobRoleTag role,
                    @RequestParam(defaultValue = "10") int limit
    ) {
        return skillAnalysisService.getTopSkills(city, role, limit);
    }

    @Operation(summary = "Get most frequent co-occurring skills", 
               description = "Returns list of skills that most frequently appear alongside the specified base skill")
    @GetMapping("/related-skills/frequency")
    public List<CoOccurrenceResultDto> getMostFrequentCoOccuringSkills(
                    @RequestParam String baseSkill,
                    @RequestParam(required = false) String city,
                    @RequestParam(required = false) JobRoleTag role,
                    @RequestParam(defaultValue = "10") int limit
    ) {
        return skillAnalysisService.getCoOccuringSkills(baseSkill, city, role, limit);
    }

    @Operation(summary = "Get most strongly associated skills", 
               description = "Returns list of skills that have strongest association to the base skill, calculated using the Jaccard Index")
    @GetMapping("/related-skills/jaccard")
    public List<CoOccurrenceResultDto> getHighestAssociatedSkills(
                    @RequestParam String baseSkill,
                    @RequestParam(required = false) String city,
                    @RequestParam(required = false) JobRoleTag role,
                    @RequestParam(defaultValue = "10") int limit
    ) {
        return skillAnalysisService.getHighestAssociatedSkills(baseSkill, city, role, limit);
    }

    @PostMapping("/skill-gap")
    public ResponseEntity<SkillGapResponseDto> performSkillGapAnalyze(
                    @Valid @RequestBody SkillGapRequestDto request
    ) {
        return ResponseEntity.ok(skillGapService.analyzeSkillGap(request));
    }




    

}
