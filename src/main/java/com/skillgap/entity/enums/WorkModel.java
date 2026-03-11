package com.skillgap.entity.enums;

public enum WorkModel {
    STATIONARY, HYBRID, REMOTE;

    public static WorkModel fromString(String text) {
        if (text == null) {
            return null;
        }

        for (WorkModel model : WorkModel.values()) {
            if (model.name().equalsIgnoreCase(text)) {
                return model;
            }
        }

        if (text.equalsIgnoreCase("office")) {
            return WorkModel.STATIONARY;
        }

        return null;

    }
}
