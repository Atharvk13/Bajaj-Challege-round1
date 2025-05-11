package com.bajaj.hiring.service;

import com.bajaj.hiring.model.SolutionRequest;
import com.bajaj.hiring.model.WebhookRequest;
import com.bajaj.hiring.model.WebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
            // Step 1: Generate webhook
            WebhookRequest request = new WebhookRequest(
                    "Atharv Kanungo",
                    "0827CI221037", // Notice: Last two digits "47" - odd number, so it's Question 1
                    "atharvkanungo220631@acropolis.in"
            );
            
            WebhookResponse response = postForWebhook(request);
            System.out.println("Webhook generated: " + response.getWebhook());
            System.out.println("Access token received: " + response.getAccessToken());
            
            // Step 2: Solve the SQL problem based on regNo
            // Since the regNo ends with "47" (odd number), we're solving Question 1
            String sqlSolution = getSqlSolutionForQuestion1();
            
            // Step 3: Submit the solution to the webhook URL
            submitSolution(response.getWebhook(), response.getAccessToken(), sqlSolution);
            
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
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
        headers.set("Content-Type", "application/json");
        
        SolutionRequest solutionRequest = new SolutionRequest(sqlSolution);
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
        // SQL Solution for Question 1
        // This is the SQL query that solves Question 1 (as per the odd regNo)
        
        // Based on the typical SQL problems for this kind of assessment, 
        // I'll provide a placeholder query. You should replace this with the actual solution
        // after analyzing the specific question details from the Google Drive link.
        
        // For the sake of this example (since we can't access the Google Drive link),
        // I'll provide a sample SQL query that might be typical for such assessments:
        
        return "SELECT department.department_name, COUNT(employee.employee_id) as employee_count " +
               "FROM department " +
               "JOIN employee ON department.department_id = employee.department_id " +
               "GROUP BY department.department_name " +
               "HAVING COUNT(employee.employee_id) > 5 " +
               "ORDER BY employee_count DESC";
    }
}