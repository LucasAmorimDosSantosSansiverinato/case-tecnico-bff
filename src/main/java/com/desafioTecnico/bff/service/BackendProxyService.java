package com.desafioTecnico.bff.service;

import com.desafioTecnico.bff.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BackendProxyService {

    private static final Logger log = LoggerFactory.getLogger(BackendProxyService.class);
    private static final String SERVICE_TOKEN_HEADER = "X-Service-Token";

    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final String backendUrl;

    public BackendProxyService(
            RestTemplate restTemplate,
            JwtService jwtService,
            @Value("${backend.url:http://localhost:8080}") String backendUrl
    ) {
        this.restTemplate = restTemplate;
        this.jwtService   = jwtService;
        this.backendUrl   = backendUrl;
    }

    // Monta headers com o service token JWT para autenticar o BFF no Backend
    private HttpHeaders headersComServiceToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SERVICE_TOKEN_HEADER, jwtService.gerarServiceToken());
        return headers;
    }

    public <T> ResponseEntity<Object> post(String path, T body) {
        String url = backendUrl + path;
        log.info("[BFF->BACKEND] POST {}", url);
        try {
            HttpEntity<T> entity = new HttpEntity<>(body, headersComServiceToken());
            ResponseEntity<Object> response = restTemplate.postForEntity(url, entity, Object.class);
            log.info("[BFF->BACKEND] POST {} - resposta: {}", url, response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            log.warn("[BFF->BACKEND] POST {} - erro do cliente: {} - body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
        } catch (HttpServerErrorException ex) {
            log.error("[BFF->BACKEND] POST {} - erro do servidor: {} - body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("detail", ex.getResponseBodyAsString()));
        } catch (ResourceAccessException ex) {
            log.error("[BFF->BACKEND] POST {} - backend inacessível: {}", url, ex.getMessage());
            return ResponseEntity.status(503).body(Map.of("detail", "Backend temporariamente indisponível. Tente novamente em alguns segundos."));
        } catch (RestClientException ex) {
            log.error("[BFF->BACKEND] POST {} - erro ao comunicar com backend: {}", url, ex.getMessage());
            return ResponseEntity.status(503).body(Map.of("detail", "Backend temporariamente indisponível. Tente novamente em alguns segundos."));
        }
    }

    public ResponseEntity<Object> get(String path) {
        String url = backendUrl + path;
        log.info("[BFF->BACKEND] GET {}", url);
        try {
            HttpEntity<Void> entity = new HttpEntity<>(headersComServiceToken());
            ResponseEntity<Object> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Object.class);
            log.info("[BFF->BACKEND] GET {} - resposta: {}", url, response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            log.warn("[BFF->BACKEND] GET {} - erro do cliente: {} - body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class));
        } catch (HttpServerErrorException ex) {
            log.error("[BFF->BACKEND] GET {} - erro do servidor: {} - body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("detail", ex.getResponseBodyAsString()));
        } catch (ResourceAccessException ex) {
            log.error("[BFF->BACKEND] GET {} - backend inacessível: {}", url, ex.getMessage());
            return ResponseEntity.status(503).body(Map.of("detail", "Backend temporariamente indisponível. Tente novamente em alguns segundos."));
        } catch (RestClientException ex) {
            log.error("[BFF->BACKEND] GET {} - erro ao comunicar com backend: {}", url, ex.getMessage());
            return ResponseEntity.status(503).body(Map.of("detail", "Backend temporariamente indisponível. Tente novamente em alguns segundos."));
        }
    }
}
