package com.example.multitenancylibrary.network;

public class MultiTenancyStorage {

    private MultiTenancyStorage() {
    }

    private static final ThreadLocal<Integer> tenant = new InheritableThreadLocal<>();

    public static void setTenantID(Integer tenantName) {
        tenant.set(tenantName);
    }

    public static Integer getTenantID() {
        return tenant.get();
    }
}

