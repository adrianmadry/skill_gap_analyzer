package com.skillgap.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
@Slf4j
public class SkillDictionaryLoader {

    private final ObjectMapper objectMapper;

    // Skill Dictionary normalized to <alias, official_name>
    private Map<String, String> normalizedDictionary = new HashMap<>();

    @PostConstruct
    public void init() {
        this.normalizedDictionary = loadAndNormalize();
        log.info("Skills dictionary has been loaded");
    }

    public Map<String, String> loadAndNormalize() {
        Map<String, String> normalizedDict = new HashMap<>(); 
        
        try {
            InputStream inputStream = getClass().getResourceAsStream("/skillsDict.json");
            Map<String, Map<String, List<String>>> rawDict = objectMapper.readValue(inputStream, 
                                        new TypeReference <Map<String, Map<String, List<String>>>>() {});
            rawDict.values().forEach(technologies -> {
                technologies.forEach((officialName, aliases) -> {
                    aliases.forEach(alias -> normalizedDict.put(alias.toLowerCase(), officialName));
                    normalizedDict.put(officialName.toLowerCase(), officialName);
                });
            });
            return normalizedDict;
                                            
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }

    public Map<String, String> getDictionary() {
        return this.normalizedDictionary;
    }

}
