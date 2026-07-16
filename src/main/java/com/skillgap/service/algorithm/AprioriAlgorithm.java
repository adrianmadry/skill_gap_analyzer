package com.skillgap.service.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


/**
 * Implementation of the Apriori algorithm for mining frequent skill itemsets
 * from job offer data.
 *
 * <p>The algorithm works in three stages:
 * <ol>
 *   <li>Count individual skill frequencies and filter by minimum support threshold</li>
 *   <li>Generate frequent 2-itemsets (pairs) from frequent single skills</li>
 *   <li>Generate frequent 3-itemsets (triplets) from frequent pairs using join and prune steps</li>
 * </ol>
 *
 * <p>Minimum support is expressed as a fraction of total offers,
 * e.g. {@code 0.05} means a skill or stack must appear in at least 5% of offers.
 *
 */
@Component
public class AprioriAlgorithm {

    /**
    * Generates frequent 2-itemsets from single frequent items.
    * Returns 2-itemsets along with their distribution
    */
    public Map<Set<Long>, Integer> createTwoElementsStacks(
                Map<Long, Set<Long>> offersWithSkills, Map<Long, Integer> skillsCounts) {

        Set<Set<Long>> skillsPairs = generateSkillPairs(skillsCounts.keySet());
        return  calculateSkillStacksDistribution(skillsPairs, offersWithSkills);
    }
    
    /**
    * Generates frequent 3-itemsets from 2-elements frequent items.
    * Returns 3-itemsets along with their distribution
    */
    public Map<Set<Long>, Integer> createThreeElementsStacks(
                Map<Set<Long>, Integer> twoElementsStacks, Map<Long, Set<Long>> offersWithSkills) {
        
        Set<Set<Long>> skillsTriplets = generateSkillTriplets(twoElementsStacks.keySet());
        Set<Set<Long>> prunedSkillsTriplets = pruneSkillTriplets(skillsTriplets, twoElementsStacks.keySet());
        return calculateSkillStacksDistribution(prunedSkillsTriplets, offersWithSkills);
    }

    /**
     * Counts how many job offers contain each skill.
     *
     * @param offersWithSkills map of offerId → set of skillIds
     * @return map of skillId → number of offers containing that skill
     */
    public Map<Long, Integer> countSkillsDistributionInOffers(Map<Long, Set<Long>> offersWithSkills) {
        Map<Long, Integer> skillsDistributionMap = new HashMap<>();

        offersWithSkills.values().forEach(skillSet -> {
            skillSet.forEach(skillId -> {
                skillsDistributionMap.merge(skillId, 1, Integer::sum);
            });
        });
        return skillsDistributionMap;    
    }

    /**
    * Filters the generated itemsets by keeping only those that meet the minimum threshold.
    * The method calculates the absolute minimum support count (the required number of occurrences) 
    * based on the total number of offers and the percentage threshold
    * 
    * @param threshold minimum support as a fraction, e.g. 0.05 = 5%
    */
    public <K> Map<K, Integer> filterByThreshold(
            Map<K, Integer> itemsets, int totalOffers, double threshold) {
    
        return itemsets.entrySet().stream()
                                    .filter((entry -> entry.getValue() >= (totalOffers * threshold)))
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    private Set<Set<Long>> pruneSkillTriplets(Set<Set<Long>> skillsTriplets, Set<Set<Long>> twoElementsStacks) {
        Set<Set<Long>> prunedTriplets = new HashSet<>();

        for (Set<Long> triplet : skillsTriplets) {
            Set<Set<Long>> pairsInsideTriplet = getAllPairsFromTriplet(triplet);
            
            if (twoElementsStacks.containsAll(pairsInsideTriplet)) {
                prunedTriplets.add(triplet);
            }
        }
        return prunedTriplets;
    }

    private Set<Set<Long>> getAllPairsFromTriplet(Set<Long> triplet) {
        Set<Set<Long>> allPairs = new HashSet<>();
        
        for (Long skillId : triplet) {
            Set<Long> subPair = new HashSet<>(triplet);
            subPair.remove(skillId);
            allPairs.add(subPair);
        }
        return allPairs;
    }

    private Map<Set<Long>, Integer> calculateSkillStacksDistribution(
                Set<Set<Long>> skillStacks, Map<Long, Set<Long>> offersWithSkills) {

        Map<Set<Long>, Integer> skillStacksDistribution = new HashMap<>();
        
        for (Set<Long> offerSkills : offersWithSkills.values()) {
            for (Set<Long> skillStack : skillStacks) {
                if (offerSkills.containsAll(skillStack)) {
                    skillStacksDistribution.merge(skillStack, 1, Integer::sum);
                }
            }
        }
        return skillStacksDistribution;
    }

    private Set<Set<Long>> generateSkillPairs(Set<Long> skillsIds) {
        
        Set<Set<Long>> skillPairs = new HashSet<>();

        List<Long> skillsIdsList = new ArrayList<>(skillsIds); // convert to indexed list for iteration

        for (int i = 0; i < skillsIdsList.size() - 1; i++) {
            for (int j = i + 1; j < skillsIdsList.size(); j++) {
                skillPairs.add(Set.of(skillsIdsList.get(i), skillsIdsList.get(j)));
            }
        }
        return skillPairs;
    }

    private Set<Set<Long>> generateSkillTriplets(Set<Set<Long>> skillPairsIds) {
        
        Set<Set<Long>> skillTriplets = new HashSet<>();
        List<Set<Long>> skillPairsList = new ArrayList<>(skillPairsIds); // convert to indexed list for iteration

        for (int i = 0; i < skillPairsList.size() - 1; i++) {
            for (int j = i + 1; j < skillPairsList.size(); j++) {
                Set<Long> tripletCandidate = new HashSet<>(skillPairsList.get(i));
                tripletCandidate.addAll(skillPairsList.get(j));

                if (tripletCandidate.size() == 3) {
                    skillTriplets.add(tripletCandidate);
                }
            }
        }
        return skillTriplets;
    }

}
