package com.skillgap.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dao.SkillRepository;
import com.skillgap.dto.SkillTotalCountDto;
import com.skillgap.dto.response.CoOccurrenceResultDto;
import com.skillgap.dto.response.SkillCoCountDto;
import com.skillgap.dto.response.SkillStatsDto;
import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.JobRoleTag;
import com.skillgap.exception.SkillNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkillAnalysisService {

    private final JobOfferRepository jobOfferRepository;
    private final SkillRepository skillRepository;
    
    @Value("${skill-analysis.co-occurrence.candidate-pool:100}")
    private int candidatePool;

    public List<SkillStatsDto> getTopSkills(String city, JobRoleTag role, int limit) {
        Pageable topN = PageRequest.of(0, limit);
        return jobOfferRepository.findTopSkillsByCityAndRole(city, role, topN);
    }

    public List<CoOccurrenceResultDto> getCoOccuringSkills(String baseSkillName, String city, JobRoleTag role, int limit) {
        return getCoOccurrenceSkillsStream(baseSkillName, city, role)
                .limit(limit)
                .toList();
    }

    public List<CoOccurrenceResultDto> getHighestAssociatedSkills(String baseSkillName, String city, JobRoleTag role, int limit) {
        return getCoOccurrenceSkillsStream(baseSkillName, city, role)
                .limit(50)
                .sorted(Comparator.comparingDouble(CoOccurrenceResultDto::coScore).reversed())
                .limit(limit)
                .toList();
    } 

    private Stream<CoOccurrenceResultDto> getCoOccurrenceSkillsStream(String baseSkillName, String city, JobRoleTag role) {
        Skill baseSkill = skillRepository.findByNameIgnoreCase(baseSkillName)
                                .orElseThrow(() -> new SkillNotFoundException(baseSkillName));
    
        long totalBaseSkillOffers = jobOfferRepository.countBySkillId(baseSkill.getId(), city, role);
        if (totalBaseSkillOffers == 0) return Stream.empty();

        Pageable limitCandidates = PageRequest.of(0, candidatePool);
        List<SkillCoCountDto> relatedSkills = jobOfferRepository.findCoOccuringSkills(baseSkill.getId(), city, role, limitCandidates);
        
        List<Long> skillsIds =  relatedSkills.stream().map(skill -> skill.id()).toList();
        Map<Long, Long> relatedSkillsCountMap = jobOfferRepository.countForMultipleSkillsIds(skillsIds, city, role)
                                                .stream()
                                                .collect(Collectors.toMap(SkillTotalCountDto::id, SkillTotalCountDto::totalCount));

        return relatedSkills.stream()
                .map(skill -> {
                    long offersWithSkill = relatedSkillsCountMap.get(skill.id());
                    double coScore = calculateStrengthOfAssociation(totalBaseSkillOffers, offersWithSkill, skill.coOccurrenceCount());
                    double percentage = calculateCoOccurrenceRate(totalBaseSkillOffers, skill.coOccurrenceCount());
                    return new CoOccurrenceResultDto(skill.skillName(), skill.coOccurrenceCount(), coScore, percentage);
                });
    }

    private double calculateStrengthOfAssociation(long baseSkillOffersCount, long secondSkillOffersCount, long bothSkillsOfferscount) {
        return (double) bothSkillsOfferscount / (baseSkillOffersCount + secondSkillOffersCount - bothSkillsOfferscount);
    }

    private double calculateCoOccurrenceRate(long baseSkillOffersCount, long bothSkillsOfferscount) {
        return (double) bothSkillsOfferscount / baseSkillOffersCount * 100;
    }


}
