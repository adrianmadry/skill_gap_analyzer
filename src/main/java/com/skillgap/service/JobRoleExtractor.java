package com.skillgap.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.skillgap.entity.enums.JobRoleTag;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobRoleExtractionService {

    private final RoleDictionaryLoader roleDictionaryLoader;

    public JobRoleTag matchRole(String jobTitle) {
        if (jobTitle == null || jobTitle.isBlank()) {
            return JobRoleTag.OTHER;
        }

        Map<JobRoleTag, List<String>> dictionary = roleDictionaryLoader.getRoleDictionary();
        
        return dictionary.entrySet().stream()
                    .map(e -> {
                        long hits = e.getValue().stream()
                                .filter(jobTitle.toLowerCase()::contains)
                                .count();
                        return Map.entry(e.getKey(), hits);  ///  roleteg, hits
                    })
                    .filter(e -> e.getValue() > 0)
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(JobRoleTag.OTHER);
                
    }
}
