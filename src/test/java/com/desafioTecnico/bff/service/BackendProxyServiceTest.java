package com.desafioTecnico.bff.service;

import com.desafioTecnico.bff.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class BackendProxyServiceTest {

    @Mock JwtService jwtService;

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private BackendProxyService service;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        // JwtService é mockado para retornar um token fictício
        when(jwtService.gerarServiceToken()).thenReturn("mock-service-token");
        service = new BackendProxyService(restTemplate, jwtService, "http://backend:8080");
    }

    @Test
    void post_happyPath_returns201() {
        server.expect(requestTo("http://backend:8080/api/v1/persons"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":\"abc\"}"));

        ResponseEntity<Object> result = service.post("/api/v1/persons", "{}");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        server.verify();
    }

    @Test
    void post_httpClientError422_relaysStatus() {
        server.expect(requestTo("http://backend:8080/api/v1/persons"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"invalid\"}"));

        ResponseEntity<Object> result = service.post("/api/v1/persons", "{}");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        server.verify();
    }

    @Test
    void post_httpClientError404_relaysStatus() {
        server.expect(requestTo("http://backend:8080/api/v1/persons"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"not found\"}"));

        ResponseEntity<Object> result = service.post("/api/v1/persons", "{}");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        server.verify();
    }

    @Test
    void get_happyPath_returns200() {
        server.expect(requestTo("http://backend:8080/api/v1/persons"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"data\":[]}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> result = service.get("/api/v1/persons");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        server.verify();
    }

    @Test
    void get_httpClientError404_relaysStatus() {
        server.expect(requestTo("http://backend:8080/api/v1/persons/999"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"not found\"}"));

        ResponseEntity<Object> result = service.get("/api/v1/persons/999");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        server.verify();
    }

    @Test
    void get_buildsCorrectUrlWithId() {
        server.expect(requestTo("http://backend:8080/api/v1/persons/42"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":\"42\"}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> result = service.get("/api/v1/persons/42");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        server.verify();
    }
}
