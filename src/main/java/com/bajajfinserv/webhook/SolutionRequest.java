package com.bajajfinserv.webhook;

public class SolutionRequest {
    private String finalQuery;

    // Default constructor
    public SolutionRequest() {}

    // Constructor with parameter
    public SolutionRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    // Getters and Setters
    public String getFinalQuery() {
        return finalQuery;
    }

    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }
}