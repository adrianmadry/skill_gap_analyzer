package com.skillgap.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillgap.dao.SkillRepository;
import com.skillgap.dao.UserRepository;
import com.skillgap.dto.response.UserSkillsUpdateResponseDto;
import com.skillgap.entity.AppUser;
import com.skillgap.entity.Skill;
import com.skillgap.exception.ResourceNotFoundException;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Transactional 
    public UserSkillsUpdateResponseDto addSkills(List<String> userSkillsNames,Long userId) {
        
        AppUser user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + userId + " not found"));

        Set<String> userSkillsNamesSet = new HashSet<>(userSkillsNames);

        // verify if Skills names provided by user exist in skills database
        List<Skill> skillsToSave = skillRepository.findAllByNameInIgnoreCase(userSkillsNamesSet); 
        Set<String> skillsToSaveNames = skillsToSave.stream()
                                                .map(Skill::getName)
                                                .collect(Collectors.toSet());

        Set<String> skillsToSaveNamesLower = skillsToSave.stream()
                                            .map(skill -> skill.getName().toLowerCase())
                                            .collect(Collectors.toSet());

        Set<String> rejectedSkillsNames = userSkillsNames.stream()
                                        .filter(s -> !skillsToSaveNamesLower.contains(s.toLowerCase()))
                                        .collect(Collectors.toSet());
        
        skillsToSave.stream()
                    .forEach(s -> user.addSkill(s));

        userRepository.save(user);
        log.info("Successfully added {} skills for user Id {}, rejected {}", userId, skillsToSave.size(), rejectedSkillsNames.size());
        log.debug("Added skills: {}", skillsToSaveNamesLower.toString());
        log.debug("Rejected skills: {}", rejectedSkillsNames);

        return new UserSkillsUpdateResponseDto(userId, skillsToSaveNames, rejectedSkillsNames);

    }

    public AppUser findByUserName(String loggedUserName) {
        return userRepository.findByEmail(loggedUserName)
                                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + loggedUserName + " not found"));
    }

}
