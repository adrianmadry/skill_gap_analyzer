package com.skillgap.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.skillgap.entity.enums.JobRoleTag;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;


@Component
@RequiredArgsConstructor
@Slf4j
public class RoleDictionaryLoader {

    private final YAMLMapper yamlMapper = YAMLMapper.builder().build();
    private Map<JobRoleTag, List<String>> roleDictionary = new HashMap<>();

    @PostConstruct
    public void init() {
        this.roleDictionary = loadDictionary();
        log.info("Role dictionary has been loaded");
    }

    public Map<JobRoleTag, List<String>> getRoleDictionary() {
        return this.roleDictionary;
    }

    private Map<JobRoleTag, List<String>> loadDictionary() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/role-dictionary.yml");
            if (inputStream == null) {
                throw new RuntimeException("Didn't find YML file in resources!");
            }
            
            Map<String, List<String>> rawDict = yamlMapper.readValue(inputStream, 
                                        new TypeReference <Map<String, List<String>>>() {});

            Map<JobRoleTag, List<String>> finalDict = new HashMap<>();
            rawDict.forEach((key, value) -> {
                try {
                    JobRoleTag roleTag = JobRoleTag.valueOf(key);
                    finalDict.put(roleTag, value);
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown role tag in dictionary: '{}", key);
                }
            });

            return finalDict;

        } catch (Exception e) {
            log.error("Failed to read role dictionary YML: ", e);
            throw new RuntimeException("Failed to read YML file" + e.getMessage());
        }
        
    }

}
