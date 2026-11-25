package com.skillgap.exception;

public class ProviderNotFoundException extends RuntimeException {

    public ProviderNotFoundException(String providerName) {
        super("No provider found with name: " + providerName);
    }
}
