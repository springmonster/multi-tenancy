package com.example.multitenancylibrary;

import com.example.multitenancylibrary.config.MultiTenancyProperties;
import com.example.multitenancylibrary.sql.TenantIDCheckerExecuteListener;
import com.example.multitenancylibrary.sql.TenantIDModifierVisitListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.VisitListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MultiTenancyProperties.class)
public class MultiTenancyAutoConfigure {

    @Autowired
    private MultiTenancyProperties multiTenancyProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "multi.tenancy", value = {"enable", "sql-auto-add-filters"}, havingValue = "true")
    public VisitListenerProvider visitListenerProvider() {
        return () -> new TenantIDModifierVisitListener(multiTenancyProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "multi.tenancy", value = {"enable", "sql-check-filters-exist"}, havingValue = "true")
    public ExecuteListenerProvider executeListenerProvider() {
        return () -> new TenantIDCheckerExecuteListener(multiTenancyProperties);
    }

}
