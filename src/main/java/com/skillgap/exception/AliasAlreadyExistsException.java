package com.skillgap.exception;

public class AliasAlreadyExistsException extends RuntimeException {

    public AliasAlreadyExistsException(String aliasName) {
        super("Alias with name '" + aliasName + "' already exists in database");
    }
}
