package com.example.springbootpostgresjooq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringbootPostgresJooqApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootPostgresJooqApplication.class, args);
    }

}
