package com.example.multitenancylibrary.sql;

public class TenantIDException extends RuntimeException {

    public TenantIDException(String message) {
        super(message);
    }
}
