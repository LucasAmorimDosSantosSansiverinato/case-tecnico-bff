package com.desafioTecnico.bff.controller;

import com.desafioTecnico.bff.dto.RegisterPersonRequest;
import com.desafioTecnico.bff.service.BackendProxyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    private final BackendProxyService proxy;

    public PersonController(BackendProxyService proxy) {
        this.proxy = proxy;
    }

    @PostMapping
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterPersonRequest request) {
        log.info("[BFF] POST /api/persons - cadastro recebido para: {}", request.getNomeCompleto());
        ResponseEntity<Object> response = proxy.post("/api/v1/persons", request);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("[BFF] Cadastro realizado com sucesso para: {}", request.getNomeCompleto());
        } else {
            log.warn("[BFF] Falha no cadastro para: {} - status: {}", request.getNomeCompleto(), response.getStatusCode());
        }
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("[BFF] GET /api/persons - listando pessoas");
        ResponseEntity<Object> response = proxy.get("/api/v1/persons");
        log.info("[BFF] Lista retornada - status: {}", response.getStatusCode());
        return response;
    }

    @GetMapping("/login/{login}")
    public ResponseEntity<Object> getByLogin(@PathVariable String login) {
        log.info("[BFF] GET /api/persons/login/{} - autenticando por login", login);
        ResponseEntity<Object> response = proxy.get("/api/v1/persons/login/" + login);
        log.info("[BFF] Login {} - status: {}", login, response.getStatusCode());
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        log.info("[BFF] GET /api/persons/{} - buscando pessoa", id);
        ResponseEntity<Object> response = proxy.get("/api/v1/persons/" + id);
        log.info("[BFF] Busca por id {} - status: {}", id, response.getStatusCode());
        return response;
    }
}
