package com.skillgap.exception.auth;

public class PasswordsDoNotMatchException extends RuntimeException {

    public PasswordsDoNotMatchException() {
        super("Password and confirm password are not the same");
    }
}
