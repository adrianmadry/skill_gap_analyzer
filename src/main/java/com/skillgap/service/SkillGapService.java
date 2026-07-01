package com.skillgap.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dao.SkillRepository;
import com.skillgap.dto.SkillRecommendationDto;
import com.skillgap.dto.SkillTotalCountDto;
import com.skillgap.dto.external.SkillGapRequestDto;
import com.skillgap.dto.response.SkillGapResponseDto;
import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.JobRoleTag;
import com.skillgap.exception.NoMarketDataException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillGapService {
    
    private final JobOfferRepository jobOfferRepository;
    private final SkillRepository skillRepository;

    @Value("${skill-gap.thresholds.must-have:50.0}")
    private double mustHaveThreshold;

    @Value("${skill-gap.thresholds.nice-to-have:15.0}")
    private double niceToHaveThreshold;

    @Value("${skill-gap.weights.must-have:0.8}")
    private double mustHaveWeight;

    @Value("${skill-gap.weights.nice-to-have:0.2}")
    private double niceToHaveWeight;

    public SkillGapResponseDto analyzeSkillGap(SkillGapRequestDto request) {
        List<String> lowerCaseUserSkills = request.userSkills().stream()
            .map(String::toLowerCase)
            .toList();
        JobRoleTag targetRole = request.targetRole();
        String city = request.city();
            
        List<Long> userSkillsIdsList = skillRepository.findIdsByNamesIgnoreCase(lowerCaseUserSkills);
        Set<Long> userSkillsIds = new HashSet<>(userSkillsIdsList);

        long totalOffersCount = jobOfferRepository.countOffersByRoleAndCity(targetRole, city);
        if (totalOffersCount == 0) {
            throw new NoMarketDataException(targetRole, city);
        }

        List<SkillTotalCountDto> skillsDistributionList = jobOfferRepository.getSkillsDistribution(
                                                                    targetRole, city);
        Map<Long, String> skillsIdNameMap = skillsDistributionList.stream()
                                                    .collect(Collectors.toMap(SkillTotalCountDto::id, SkillTotalCountDto::skillName));

        // split to categories with distributin values
        MarketSkillCategories categorizedMarketSkills = categorizeMarketSkills(
                                targetRole, city, totalOffersCount, skillsDistributionList);

        log.debug("User skill IDs found in the database: {}", userSkillsIds);
        log.debug("Market Must-Have IDs: {}", categorizedMarketSkills.mustHaveDistribution().keySet());
        log.debug("Market Nice-to-Have IDs: {}", categorizedMarketSkills.niceToHaveDistribution().keySet());

        double percentageMatch = calculatePercentageMatch(userSkillsIds, categorizedMarketSkills);

        MissingSkills userMissingSkillsIds = calculateUserMissingSkills(userSkillsIds, categorizedMarketSkills);
        List<String> missingMustHaveNames = userMissingSkillsIds.missingMustHave().stream()
                                                            .map(skillId -> skillsIdNameMap.get(skillId))
                                                            .toList();
        List<String> missingNiceToHaveNames = userMissingSkillsIds.missingNiceHave().stream()
                                                    .map(skillId -> skillsIdNameMap.get(skillId))
                                                    .toList();

        List<SkillRecommendationDto> recommendedSkills = generateRecommendedSkills(userSkillsIds, userMissingSkillsIds, targetRole, city);

        return new SkillGapResponseDto(
            percentageMatch, 
            missingMustHaveNames,  
            missingNiceToHaveNames, 
            recommendedSkills
        );
    };

    private List<SkillRecommendationDto> generateRecommendedSkills(
                          Set<Long> userSkillsIds, MissingSkills userMissingSkills, JobRoleTag roleTag, String city) {
        
        List<Long> allUserMissingSkills = new ArrayList<>(userMissingSkills.missingMustHave());
        allUserMissingSkills.addAll(userMissingSkills.missingNiceHave());

        Map<Long, Long> skillsRecScoreMap = skillRepository.findRecommendationScoresBatch(allUserMissingSkills, userSkillsIds, roleTag, city)
                                                            .stream()
                                                            .collect(Collectors.toMap(
                                                                row -> (Long) row[0],
                                                                row -> (Long) row[1]
                                                            ));

        log.debug("Skill Rec Score Map: {}", skillsRecScoreMap);


        List<Map.Entry<Long, Long>> top5SkillsByRecScore = skillsRecScoreMap.entrySet().stream()
                                                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                                    .limit(5)
                                                    .toList();

        Set<Long> top5SkillsIds = top5SkillsByRecScore.stream().map(Map.Entry::getKey).collect(Collectors.toSet());

        Map<Long, String> skillNamesMap = skillRepository.findAllByIdIn(top5SkillsIds).stream()
                                                    .collect(Collectors.toMap(Skill::getId, Skill::getName));

        return top5SkillsByRecScore.stream()
                    .map(skill -> createSkillRecommendationDto(skill, skillNamesMap, userMissingSkills)
                    )
                    .toList();
    }

    private SkillRecommendationDto createSkillRecommendationDto(
                    Map.Entry<Long, Long> skill, Map<Long, String> skillNamesMap, MissingSkills userMissingSkills) {

        Long skillId = skill.getKey();
        long recScore = skill.getValue();
        
        String skillName = skillNamesMap.get(skillId);
        String category = userMissingSkills.missingMustHave().contains(skillId) 
                                    ? "MUST_HAVE" 
                                    : "NICE_TO_HAVE";

        return new SkillRecommendationDto(skillId, skillName, recScore, category);
    }

    private MarketSkillCategories categorizeMarketSkills(
                    JobRoleTag role, String city, long totalOffersCount, List<SkillTotalCountDto> skillsDistributionList) {
    
        Map<Long, Long> mustHaveDistribution = new HashMap<>();
        Map<Long, Long> niceToHaveDistribution = new HashMap<>();
        MarketSkillCategories marketSkillCategories = new MarketSkillCategories(mustHaveDistribution, niceToHaveDistribution);

        skillsDistributionList.forEach(dto -> {
            double skillPopularityValue = calculateSkillPopularity(dto.totalCount(), totalOffersCount);

            // update market skill categories object
            classifySkill(dto, skillPopularityValue, marketSkillCategories);
            
        });

        return marketSkillCategories;

    }

    private MissingSkills calculateUserMissingSkills (Set<Long> userSkillsIds, MarketSkillCategories categorizedMarketSkills) {
        
        List<Long> misingMustHave = findMissingSkillsFromCategory(userSkillsIds, categorizedMarketSkills.mustHaveDistribution());
        List<Long> misingNiceHave = findMissingSkillsFromCategory(userSkillsIds, categorizedMarketSkills.niceToHaveDistribution());

        return new MissingSkills(misingMustHave, misingNiceHave);
    }

    private double calculatePercentageMatch(Set<Long> userSkillsIds, MarketSkillCategories categorizedMarketSkills) {
            
        double mustHaveTotalWeight = categorizedMarketSkills.mustHaveDistribution().values().stream()
                                        .mapToDouble(Long::doubleValue)
                                        .sum();

        double niceHaveTotalWeight = categorizedMarketSkills.niceToHaveDistribution().values().stream()
                                        .mapToDouble(Long::doubleValue)
                                        .sum();

        double userMustHaveWeight = categorizedMarketSkills.mustHaveDistribution().entrySet().stream()
                                        .filter(marketSkill -> userSkillsIds.contains(marketSkill.getKey()))
                                        .mapToDouble(e -> e.getValue().doubleValue())
                                        .sum();
                                        
        double userNiceHaveWeight = categorizedMarketSkills.niceToHaveDistribution().entrySet().stream()
                                .filter(marketSkill -> userSkillsIds.contains(marketSkill.getKey()))
                                .mapToDouble(e -> e.getValue().doubleValue())
                                .sum();

        double mustHaveScore = mustHaveTotalWeight == 0 ? 0.0 
                                : (userMustHaveWeight / mustHaveTotalWeight * 100);

        double niceHaveScore = niceHaveTotalWeight == 0 ? 0.0 
                        : (userNiceHaveWeight / niceHaveTotalWeight * 100);

        double finalScore = (mustHaveScore * mustHaveWeight) + (niceHaveScore * niceToHaveWeight);
        
        return Math.round(finalScore * 100.0) / 100.0;
        
    }

    private double calculateSkillPopularity(Long offersWithSkillCount, Long totalOffersCount) {
        if (totalOffersCount == null || totalOffersCount == 0) return 0.0;
        return (double) offersWithSkillCount / totalOffersCount * 100;
    }

    private void classifySkill (SkillTotalCountDto skill, double skillPopularityValue, MarketSkillCategories marketSkillCategories) {
  
        if (skillPopularityValue >= mustHaveThreshold) {
                marketSkillCategories.mustHaveDistribution().put(skill.id(), skill.totalCount());
            } else if (skillPopularityValue >= niceToHaveThreshold) {
                marketSkillCategories.niceToHaveDistribution().put(skill.id(), skill.totalCount());
            } 
    }

    private List<Long> findMissingSkillsFromCategory(Set<Long> userSkillsSet, Map<Long, Long> categoryMarketSkills) {
        if (categoryMarketSkills == null) return Collections.emptyList();

        return categoryMarketSkills.keySet().stream()
                            .filter(marketSkillId -> !userSkillsSet.contains(marketSkillId))
                            .toList();
    }

    /**
     * Skills that the user is missing compared to market requirements.
     * Contains skill IDs
     */
    private record MissingSkills(List<Long> missingMustHave, List<Long> missingNiceHave) {};

    /**
     * Market skills split into categories based on popularity thresholds.
     * Map key: skillId, Map value: number of job offers containing the skill.
    */
    private record MarketSkillCategories(Map<Long, Long> mustHaveDistribution, Map<Long, Long> niceToHaveDistribution) {};



}
