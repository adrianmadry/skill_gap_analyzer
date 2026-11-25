package com.skillgap.exception;

public class SkillAlreadyExistsException extends RuntimeException {
    
    public SkillAlreadyExistsException(String skillName) {
        super("Skill with name '" + skillName + "' already exists in database");
    }
}
