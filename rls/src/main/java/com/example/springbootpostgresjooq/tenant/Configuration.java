package com.example.springbootpostgresjooq.tenant;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Configuration {
    @Bean
    public RlsConnectionProvider connectionProvider() {
        return new RlsConnectionProvider();
    }
}
