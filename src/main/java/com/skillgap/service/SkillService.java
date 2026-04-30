package com.skillgap.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skillgap.dao.SkillRepository;
import com.skillgap.entity.Skill;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional
    public Set<Skill> getFromDbOrCreate(Set<String> skillNames) {
        if (skillNames == null || skillNames.isEmpty()) {
            return new HashSet<>();
        }
        List<Skill> existingSkills = skillRepository.findAllByNameInIgnoreCase(skillNames);
        Map<String, Skill> skillMap = existingSkills.stream()
                                        .collect(Collectors.toMap(s -> s.getName().toLowerCase(), s -> s));

        // Create new Set with skills exists in DB and later add the new skills saved to db
        Set<Skill> allSkills = new HashSet<>(existingSkills);

        List<Skill> newSkillsToSave = skillNames.stream()
                                    .filter(name -> !skillMap.containsKey(name.toLowerCase()))
                                    .map(name -> {
                                        Skill s = new Skill();
                                        s.setName(name);
                                        return s;
                                    })
                                    .toList();

        if (!newSkillsToSave.isEmpty()) {
            allSkills.addAll(skillRepository.saveAll(newSkillsToSave));
        }

        return allSkills;
    }
}
