package com.skillgap.entity.enums;

public enum ExperienceLevel {
    JUNIOR, MID, SENIOR, INTERN;

    public static ExperienceLevel fromString(String text) {
    if (text == null) {
        return null;
    }

    for (ExperienceLevel expLevel : ExperienceLevel.values()) {
        if (expLevel.name().equalsIgnoreCase(text)) {
            return expLevel;
        }
    }

    return null;

    }
}
