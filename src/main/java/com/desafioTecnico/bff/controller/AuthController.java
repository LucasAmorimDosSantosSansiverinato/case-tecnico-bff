package com.desafioTecnico.bff.controller;

import com.desafioTecnico.bff.security.JwtService;
import com.desafioTecnico.bff.service.BackendProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final BackendProxyService proxy;
    private final JwtService jwtService;

    public AuthController(BackendProxyService proxy, JwtService jwtService) {
        this.proxy      = proxy;
        this.jwtService = jwtService;
    }

    // Autentica o usuário pelo login e retorna um JWT + dados da pessoa
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        if (login == null || login.isBlank() || !login.matches("[a-z]{7}")) {
            return ResponseEntity.badRequest().body(Map.of("detail", "Login deve ter exatamente 7 letras minúsculas"));
        }

        log.info("[BFF-AUTH] Tentativa de login para: {}", login);

        // Consulta o backend para verificar se o login existe
        ResponseEntity<Object> backendResponse = proxy.get("/api/v1/persons/login/" + login);

        if (!backendResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("[BFF-AUTH] Login não encontrado: {}", login);
            return ResponseEntity.status(backendResponse.getStatusCode()).body(backendResponse.getBody());
        }

        // Extrai os dados da pessoa retornados pelo backend
        @SuppressWarnings("unchecked")
        Map<String, Object> pessoa = (Map<String, Object>) backendResponse.getBody();
        String personId    = String.valueOf(pessoa.get("id"));
        String nomeCompleto = String.valueOf(pessoa.get("nomeCompleto"));

        String token = jwtService.gerarTokenUsuario(login, personId, nomeCompleto);
        log.info("[BFF-AUTH] Login realizado com sucesso para: {}", login);

        return ResponseEntity.ok(Map.of(
                "token",  token,
                "person", pessoa
        ));
    }
}
