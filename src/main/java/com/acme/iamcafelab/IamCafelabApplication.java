package com.acme.iamcafelab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IamCafelabApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamCafelabApplication.class, args);
    }
}