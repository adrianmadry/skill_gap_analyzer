package com.skillgap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SkillNotFoundException extends RuntimeException {

    public SkillNotFoundException(String skillName) {
        super(String.format("Skill with name '%s' not found in the database", skillName));
    }
}