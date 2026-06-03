package com.desafioTecnico.bff.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class BackendProxyService {

    private static final Logger log = LoggerFactory.getLogger(BackendProxyService.class);

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
        String url = backendUrl + path;
        log.info("[BFF->BACKEND] POST {}", url);
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(url, body, Object.class);
            log.info("[BFF->BACKEND] POST {} - resposta: {}", url, response.getStatusCode());
            return response;
        } catch (HttpClientErrorException ex) {
            log.warn("[BFF->BACKEND] POST {} - erro do cliente: {} - body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
        } catch (HttpServerErrorException ex) {
            log.error("[BFF->BACKEND] POST {} - erro do servidor: {} - body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
        } catch (ResourceAccessException ex) {
            log.error("[BFF->BACKEND] POST {} - backend inacessível: {}", url, ex.getMessage());
            return ResponseEntity.status(503).body("Backend unavailable: " + ex.getMessage());
        }
    }

    public ResponseEntity<Object> get(String path) {
        String url = backendUrl + path;
        log.info("[BFF->BACKEND] GET {}", url);
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            log.info("[BFF->BACKEND] GET {} - resposta: {}", url, response.getStatusCode());
            return response;
        } catch (HttpClientErrorException ex) {
            log.warn("[BFF->BACKEND] GET {} - erro do cliente: {} - body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
        } catch (HttpServerErrorException ex) {
            log.error("[BFF->BACKEND] GET {} - erro do servidor: {} - body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
        } catch (ResourceAccessException ex) {
            log.error("[BFF->BACKEND] GET {} - backend inacessível: {}", url, ex.getMessage());
            return ResponseEntity.status(503).body("Backend unavailable: " + ex.getMessage());
        }
    }
}
