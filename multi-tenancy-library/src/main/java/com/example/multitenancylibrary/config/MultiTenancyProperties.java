package com.example.multitenancylibrary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "multi.tenancy")
public class MultiTenancyProperties {
    @Value("${enabled:false}")
    private boolean enabled;
    @Value("${sql-check-filters-exist:false}")
    private boolean sqlCheckFiltersExist;
    @Value("${sql-auto-add-filters:false}")
    private boolean sqlAutoAddFilters;

    @Value("#{'${tables:}'.empty ? null: '${tables:}'}")
    private List<String> tables;

    @Value("${tenant-identifier:tenant_id}")
    private String tenantIdentifier;

    public boolean isSqlCheckFiltersExist() {
        return sqlCheckFiltersExist;
    }

    public void setSqlCheckFiltersExist(boolean sqlCheckFiltersExist) {
        this.sqlCheckFiltersExist = sqlCheckFiltersExist;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSqlAutoAddFilters() {
        return sqlAutoAddFilters;
    }

    public void setSqlAutoAddFilters(boolean sqlAutoAddFilters) {
        this.sqlAutoAddFilters = sqlAutoAddFilters;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public String getTenantIdentifier() {
        return tenantIdentifier;
    }

    public void setTenantIdentifier(String tenantIdentifier) {
        this.tenantIdentifier = tenantIdentifier;
    }
}
