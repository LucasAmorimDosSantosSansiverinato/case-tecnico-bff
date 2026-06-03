package com.desafioTecnico.bff.controller;

import com.desafioTecnico.bff.dto.RegisterPersonRequest;
import com.desafioTecnico.bff.service.BackendProxyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final BackendProxyService proxy;

    public PersonController(BackendProxyService proxy) {
        this.proxy = proxy;
    }

    @PostMapping
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterPersonRequest request) {
        return proxy.post("/api/v1/persons", request);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return proxy.get("/api/v1/persons");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        return proxy.get("/api/v1/persons/" + id);
    }
}
