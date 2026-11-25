package com.skillgap.exception;

import com.skillgap.entity.enums.JobRoleTag;

public class NoMarketDataException extends RuntimeException {
    public NoMarketDataException(JobRoleTag role, String city) {
        super("No market data found for role: " + role + 
              (city != null ? " in city: " + city : ""));
    }
}
