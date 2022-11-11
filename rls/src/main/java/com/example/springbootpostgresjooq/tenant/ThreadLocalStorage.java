package com.example.springbootpostgresjooq.tenant;

public class ThreadLocalStorage {

    private static final ThreadLocal<String> tenant = new InheritableThreadLocal<>();

    public static void setTenantID(String tenantName) {
        tenant.set(tenantName);
    }

    public static String getTenantID() {
        return tenant.get();
    }
}

