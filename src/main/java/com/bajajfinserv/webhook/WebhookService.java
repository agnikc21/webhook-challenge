package com.bajajfinserv.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String TEST_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    private final RestTemplate restTemplate;

    public WebhookService() {
        this.restTemplate = new RestTemplate();
    }

    public void processWebhookFlow() {
        try {
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse != null) {
                logger.info("Webhook generated successfully");
                
                String sqlQuery = solveSqlProblem();
                
                submitSolution(webhookResponse.getAccessToken(), sqlQuery);
            }
            
        } catch (Exception e) {
            logger.error("Error in webhook flow: ", e);
        }
    }

    private WebhookResponse generateWebhook() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            WebhookRequest request = new WebhookRequest();
            request.setName("John Doe");
            request.setRegNo("REG12347");
            request.setEmail("john@example.com");

            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

            logger.info("Sending POST request to generate webhook...");
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                GENERATE_WEBHOOK_URL, entity, WebhookResponse.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Webhook generation successful");
                return response.getBody();
            } else {
                logger.error("Failed to generate webhook. Status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error generating webhook: ", e);
        }
        return null;
    }

    private String solveSqlProblem() {
        logger.info("Solving Question 2 - Count younger employees by department");
        return getSqlQuery();
    }

    private String getSqlQuery() {
        String sqlQuery = """
            SELECT 
                e1.EMP_ID,
                e1.FIRST_NAME,
                e1.LAST_NAME,
                d.DEPARTMENT_NAME,
                COUNT(e2.EMP_ID) as YOUNGER_EMPLOYEES_COUNT
            FROM EMPLOYEE e1
            JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
            LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT 
                AND e2.DOB > e1.DOB
            GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME
            ORDER BY e1.EMP_ID DESC;
            """;
        
        logger.info("Question 2 SQL Query: {}", sqlQuery);
        return sqlQuery;
    }

    private void submitSolution(String accessToken, String sqlQuery) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            SolutionRequest solutionRequest = new SolutionRequest();
            solutionRequest.setFinalQuery(sqlQuery);

            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);

            logger.info("Submitting solution...");
            ResponseEntity<String> response = restTemplate.postForEntity(
                TEST_WEBHOOK_URL, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Solution submitted successfully: {}", response.getBody());
            } else {
                logger.error("Failed to submit solution. Status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error submitting solution: ", e);
        }
    }
}
