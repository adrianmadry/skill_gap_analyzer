package com.skillgap.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skillgap.integration.common.JobOfferDto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillExtractionService {

    private final SkillDictionaryLoader skillDictionaryLoader;
    private Map<String, String> aliasToSkillName;
    private Pattern allSkillsPattern;
    private final Map<String, Integer> unknownSkillCounter = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this.aliasToSkillName = skillDictionaryLoader.getDictionary();
        this.allSkillsPattern = buildAllSkillsPattern(aliasToSkillName);
        log.info("Skills Pattern has been loaded");
    }

    public Set<String> extractSkillsFromOfferDescription(String description) {
        if (description == null || description.isBlank()) {
            return new HashSet<>();
        }
       
        Set<String> extractedSkills = new HashSet<>();
        Matcher matcher = allSkillsPattern.matcher(description);

        while (matcher.find()) {
            String foundAlias = matcher.group().toLowerCase().trim();
            String skillName = aliasToSkillName.get(foundAlias);
            if (skillName != null) {
                extractedSkills.add(skillName);
            }
        }
        return extractedSkills;
    }

    // Extract required skills from DTO class field
    public Set<String> extractRequiredSkills(JobOfferDto dto) {
        Set<String> requiredSkillsFromDict = new HashSet<>();

        for (String skill : dto.getRequiredSkills()) {
            if (skill == null) continue;

            String cleanedSkillName = skill.toLowerCase().trim();
            String skillNameInDictionary = this.aliasToSkillName.get(cleanedSkillName);

            if (skillNameInDictionary != null) {
                requiredSkillsFromDict.add(skillNameInDictionary);
            } else {
                this.unknownSkillCounter.merge(cleanedSkillName, 1, Integer::sum);
            }
        }
        return requiredSkillsFromDict;
    }

    private Pattern buildAllSkillsPattern(Map<String,String> dictionary) {
        String joinedAliases = dictionary.keySet().stream()
                                                .sorted(Comparator.comparingInt(String::length).reversed())
                                                .map(Pattern::quote)
                                                .collect(Collectors.joining("|"));
        String regex = "(?<=^|\\s|[({\\[,;])(" + joinedAliases + ")(?=\\s|$|[.,!?;)\\]}])";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public Map<String, Integer> getUnknownSkillsCounter() {
        return unknownSkillCounter.entrySet()
                                .stream()
                                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                .collect(Collectors.toMap(
                                    Map.Entry::getKey, 
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new
                                ));
    }


}
