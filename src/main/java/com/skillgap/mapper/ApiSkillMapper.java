package com.skillgap.mapper;

import org.springframework.stereotype.Component;

import com.skillgap.dto.response.SkillResponseDto;
import com.skillgap.entity.Skill;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class ApiSkillMapper {

    public SkillResponseDto mapFromEntityToDto(Skill entity) {
        if (entity == null) return null;

        SkillResponseDto dto = new SkillResponseDto();

        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setName(entity.getName());

        return dto;
        
    }
}
