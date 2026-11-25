package com.skillgap.service;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dao.SkillRepository;
import com.skillgap.dto.response.SkillStackDto;
import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.JobRoleTag;
import com.skillgap.exception.NoMarketDataException;
import com.skillgap.service.algorithm.AprioriAlgorithm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillStackService {

    private final JobOfferRepository jobOfferRepository;
    private final SkillRepository skillRepository;
    private final AprioriAlgorithm aprioriAlgorithm;

    @Value("${skill-analysis.role-stacks.popularity-threshold:0.05}")
    private double popularityThreshold;


    public List<SkillStackDto> getSkillsStackForRole(JobRoleTag role, String city) {
        
        Map<Long, Set<Long>> offersWithSkills = jobOfferRepository.getOffersWithSkillsSet(role, city)
                                                    .stream()
                                                    .collect(Collectors.toMap(
                                                        row -> (Long) row[0],
                                                        row -> {
                                                            Long[] skills = (Long[]) row[1];
                                                            return new HashSet<>(Arrays.asList(skills));
                                                        }
                                                    ));
        if (offersWithSkills.isEmpty()) {
            throw new NoMarketDataException(role, city);
        }

        log.info("Starting stack analysis for role: {}, city: {}. Fetched {} offers.", role, city, offersWithSkills.size());

        Map<Long, Integer> skillsCounts = aprioriAlgorithm.countSkillsDistributionInOffers(offersWithSkills);
        Map<Long, Integer> filteredSkillsCounts = aprioriAlgorithm.filterByThreshold(skillsCounts, offersWithSkills.size(), popularityThreshold);

        log.debug("Apriori Step 1: Found {} single frequent skills.", filteredSkillsCounts.size());        

        Map<Set<Long>, Integer> twoElementsStacks = aprioriAlgorithm.createTwoElementsStacks(offersWithSkills, filteredSkillsCounts);
        Map<Set<Long>, Integer> filteredPairs = aprioriAlgorithm.filterByThreshold(twoElementsStacks, offersWithSkills.size(), popularityThreshold);

        log.debug("Apriori Step 2: Found {} frequent skills pairs.", filteredPairs.size());
        

        Map<Set<Long>, Integer> threeElementStacks = aprioriAlgorithm.createThreeElementsStacks(filteredPairs, offersWithSkills);
        Map<Set<Long>, Integer> filteredTriplets = aprioriAlgorithm.filterByThreshold(threeElementStacks, offersWithSkills.size(), popularityThreshold);
        
        log.debug("Apriori Step 3: Found {} frequent skills triplets.", filteredTriplets.size());
        
        Map<Set<Long>, Integer> topTripletsStacks = filteredTriplets.entrySet().stream()
                                                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                                        .limit(5)
                                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Set<Long> allSkillIds = topTripletsStacks.keySet().stream()
                                            .flatMap(Set::stream)
                                            .collect(Collectors.toSet());

        Map<Long, String> skillNamesMap = skillRepository.findAllByIdIn(allSkillIds).stream()
                                            .collect(Collectors.toMap(Skill::getId, Skill::getName));

        log.info("Stack analysis complete. Found {} stacks for role: {}, city: {}", topTripletsStacks.size(), role, city);

        return topTripletsStacks.entrySet().stream()
                        .map(entry -> buildSkillStackDto(entry, skillNamesMap, offersWithSkills.size()))
                        .toList();

    }

    private SkillStackDto buildSkillStackDto(
                            Entry<Set<Long>, Integer> skillStack, Map<Long, String> skillNamesMap, int totalOffers) {
        
        List<String> skills = skillStack.getKey().stream()
                                    .map(skillNamesMap::get)
                                    .toList();

        int count = skillStack.getValue();

        double percentage = totalOffers == 0 ? 0.0 : 
                                    Math.round(((double) count / totalOffers * 100) * 100.0) / 100.0;

        return new SkillStackDto(skills, count, percentage);
        
    }

}
