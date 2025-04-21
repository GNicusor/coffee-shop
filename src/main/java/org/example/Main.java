package org.example;

//in domain folder is the place where classes are defined , repository folder contains the
//interfaces of classes defined in domain

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"org.example", "server", "service", "domain", "repository", "config", "shared"})
@EntityScan(basePackages = "domain")
@EnableJpaRepositories(basePackages = "repository")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}