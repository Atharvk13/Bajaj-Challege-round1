package com.example.demo;  // Use your actual package here

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.bajaj.hiring.service.WebhookService;

@Configuration
public class BajajHiringApplication {

    @Autowired
    private WebhookService webhookService;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            System.out.println("========== Application started. Generating webhook... ==========");
            try {
                webhookService.generateWebhookAndSolve();
            } catch (Exception e) {
                System.err.println("Error in CommandLineRunner: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}