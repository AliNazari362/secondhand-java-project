package com.secondhand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Entry point for the Secondhand Spring Boot application.
 *
 * <p>This class bootstraps the Spring application context, enabling JPA repositories
 * under {@code com.secondhand.repository} and scanning JPA entities under
 * {@code com.secondhand.entity}.</p>
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.secondhand.repository")
@EntityScan(basePackages = "com.secondhand.entity")
public class Application {

    /**
     * Main method that launches the Spring Boot application.
     *
     * @param args command-line arguments passed to the application at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("Backend Server Started on http://localhost:8080");
    }
}
