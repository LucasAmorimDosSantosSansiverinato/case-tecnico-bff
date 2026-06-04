package com.desafioTecnico.bff.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

// Responsável por emitir e validar todos os tokens JWT do BFF
@Service
public class JwtService {

    private static final long USER_TOKEN_EXPIRY_MS  = 8 * 60 * 60 * 1000L; // 8 horas
    private static final long SERVICE_TOKEN_EXPIRY_MS = 30 * 1000L;          // 30 segundos

    private final SecretKey userKey;
    private final SecretKey serviceKey;

    public JwtService(
            @Value("${jwt.secret:changeme-jwt-secret-32-chars-minimum}") String jwtSecret,
            @Value("${service.token.secret:changeme-service-secret-32-chars-min}") String serviceSecret
    ) {
        this.userKey    = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.serviceKey = Keys.hmacShaKeyFor(serviceSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Gera o token do usuário após autenticação bem-sucedida
    public String gerarTokenUsuario(String login, String personId, String nomeCompleto) {
        Date agora   = new Date();
        Date expiracao = new Date(agora.getTime() + USER_TOKEN_EXPIRY_MS);

        return Jwts.builder()
                .subject(login)
                .claims(Map.of("personId", personId, "nome", nomeCompleto))
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(userKey)
                .compact();
    }

    // Valida o token do usuário e retorna as claims; lança JwtException se inválido
    public Claims validarTokenUsuario(String token) {
        return Jwts.parser()
                .verifyWith(userKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Gera um service token de curta duração para autenticar o BFF no Backend
    public String gerarServiceToken() {
        Date agora     = new Date();
        Date expiracao = new Date(agora.getTime() + SERVICE_TOKEN_EXPIRY_MS);

        return Jwts.builder()
                .subject("bff-service")
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(serviceKey)
                .compact();
    }

    public boolean tokenValido(String token) {
        try {
            validarTokenUsuario(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
