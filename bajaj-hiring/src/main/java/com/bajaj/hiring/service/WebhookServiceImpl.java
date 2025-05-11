package com.bajaj.hiring.service;

import com.bajaj.hiring.model.SolutionRequest;
import com.bajaj.hiring.model.WebhookRequest;
import com.bajaj.hiring.model.WebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookServiceImpl implements WebhookService {

    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void generateWebhookAndSolve() {
        try {
            WebhookRequest request = new WebhookRequest(
                    "Atharv Kanungo",
                    "0827CI221037",
                    "atharvkanungo220631@acropolis.in"
            );

            WebhookResponse response = postForWebhook(request);
            if (response == null || response.getWebhook() == null || response.getAccessToken() == null) {
                throw new RuntimeException("Invalid webhook generation response.");
            }

            System.out.println("Webhook: " + response.getWebhook());
            System.out.println("Access Token: " + response.getAccessToken());

            String sqlSolution = getSqlSolutionForQuestion1();
            submitSolution(response.getWebhook(), response.getAccessToken(), sqlSolution);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WebhookResponse postForWebhook(WebhookRequest request) {
        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request);
        ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                GENERATE_WEBHOOK_URL,
                HttpMethod.POST,
                entity,
                WebhookResponse.class
        );
        return response.getBody();
    }

    private void submitSolution(String webhookUrl, String accessToken, String sqlSolution) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        SolutionRequest solutionRequest = new SolutionRequest();
        HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        System.out.println("Solution submitted. Response: " + response.getBody());
    }

    private String getSqlSolutionForQuestion1() {
        // Actual SQL based on BFHL_Q1_Question_Java 4.pdf
        return "SELECT d.name AS department_name, e.name AS employee_name " +
               "FROM Employee e " +
               "JOIN Department d ON e.department_id = d.id " +
               "WHERE e.salary = (" +
               "    SELECT MAX(e2.salary) " +
               "    FROM Employee e2 " +
               "    WHERE e2.department_id = e.department_id" +
               ")";
    }
}

@Configuration
class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
