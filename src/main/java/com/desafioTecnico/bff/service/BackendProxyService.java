package com.desafioTecnico.bff.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class BackendProxyService {

    private final RestTemplate restTemplate;
    private final String backendUrl;

    public BackendProxyService(
            RestTemplate restTemplate,
            @Value("${backend.url:http://localhost:8080}") String backendUrl
    ) {
        this.restTemplate = restTemplate;
        this.backendUrl = backendUrl;
    }

    public <T> ResponseEntity<Object> post(String path, T body) {
        try {
            return restTemplate.postForEntity(backendUrl + path, body, Object.class);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
        }
    }

    public ResponseEntity<Object> get(String path) {
        try {
            return restTemplate.getForEntity(backendUrl + path, Object.class);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
        }
    }
}
