package com.skillgap.service;

import com.skillgap.dao.SkillAliasRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.skillgap.dao.SkillRepository;
import com.skillgap.dto.external.CreateSkillRequestDto;
import com.skillgap.dto.response.SkillResponseDto;
import com.skillgap.entity.Skill;
import com.skillgap.exception.AliasAlreadyExistsException;
import com.skillgap.exception.GlobalExceptionHandler;
import com.skillgap.exception.ResourceNotFoundException;
import com.skillgap.exception.SkillAlreadyExistsException;
import com.skillgap.mapper.ApiSkillMapper;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillAliasRepository skillAliasRepository;
    private final SkillRepository skillRepository;
    private final SkillDictionaryLoader skillDictionaryLoader;
    private final ApiSkillMapper apiSkillMapper;

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

    @Transactional 
    public Long addNewSkill(CreateSkillRequestDto request) {
        String skillName = request.skillName();

        if (skillRepository.existsByNameIgnoreCase(skillName)) {
            throw new SkillAlreadyExistsException(skillName);
        }

        Skill skill = new Skill(skillName);
        request.aliases().forEach(alias -> {
            if (skillAliasRepository.existsByAliasIgnoreCase(alias)) {
                throw new AliasAlreadyExistsException(alias);
            }
            skill.addAlias(alias.toLowerCase());
        });

        Skill savedSkill = skillRepository.save(skill);
        skillDictionaryLoader.reload();

        return savedSkill.getId();

    }

    public SkillResponseDto getSkillById(Long id) {
        return skillRepository.findById(id)
                                .map(apiSkillMapper::mapFromEntityToDto)
                                .orElseThrow(() -> new ResourceNotFoundException("Skill with ID: " + id + " not found"));


    }
}
