package com.skillgap.dto.response;

import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.SkillCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponseDto {

    private Long id;
    private String description;
    private SkillCategory category;
    private String name;
}
