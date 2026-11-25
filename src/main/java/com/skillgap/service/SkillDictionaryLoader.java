package com.skillgap.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillgap.dao.SkillAliasRepository;
import com.skillgap.dao.SkillRepository;
import com.skillgap.entity.Skill;


@Component
@RequiredArgsConstructor
@Slf4j
public class SkillDictionaryLoader {

    private final ObjectMapper objectMapper;
    private final SkillAliasRepository skillAliasRepository;
    private final SkillRepository skillRepository;

    // Skill Dictionary normalized to <alias, official_name>
    private Map<String, String> normalizedDictionary = new HashMap<>();

    @PostConstruct 
    public void init() {
        if (skillRepository.count() == 0) {
            loadFromJson();
        }
        this.normalizedDictionary = loadAndNormalize();
        log.info("Skill's aliases dictionary has been loaded");
    }
        
    public Map<String, String> loadAndNormalize() {
        Map<String, String> normalizedDict = new HashMap<>(); 

        skillAliasRepository.findAllWithSkill().forEach(alias -> {
            String officialName = alias.getSkill().getName();
            normalizedDict.put(alias.getAlias().toLowerCase(), officialName);
        });

        return normalizedDict;

    }

    public void loadFromJson() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/skillsDict.json");
            if (inputStream == null) {
                throw new RuntimeException("skillsDict.json not found in resources!");
            }

            Map<String, Map<String, List<String>>> rawDict = objectMapper.readValue(
                                        inputStream, 
                                        new TypeReference <Map<String, Map<String, List<String>>>>() {}
            );

            List<Skill> skillsToSave = new ArrayList<>();

            rawDict.values().forEach(technologies -> {
                technologies.forEach((officialName, aliases) -> {
                    Skill skill = buildSkillWithAliases(officialName, aliases);
                    skillsToSave.add(skill);
                });
            });
            skillRepository.saveAll(skillsToSave);

            log.info("Loading skill aliases from json file completed");
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }

    }

    private Skill buildSkillWithAliases(String skillName, List<String> aliases) {
        Skill skill = new Skill(skillName);
        
        aliases.forEach(alias -> {
            skill.addAlias(alias);
        });
        return skill;

    }

    public void reload() {
        this.normalizedDictionary = loadAndNormalize();
        log.info("Skills dictionary reloaded with new entries");
    }

    public Map<String, String> getDictionary() {
        return this.normalizedDictionary;
    }

}
