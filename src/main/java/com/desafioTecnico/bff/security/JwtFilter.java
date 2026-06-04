package com.desafioTecnico.bff.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Protege rotas que exigem usuário autenticado validando o Bearer token
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String method = request.getMethod();
        String uri    = request.getRequestURI();

        // Rotas públicas: login e cadastro não exigem token
        if (isPublica(method, uri)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[BFF-AUTH] Requisição sem token para {} {}", method, uri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token de autenticação obrigatório");
            return;
        }

        String token = authHeader.substring(7);
        try {
            jwtService.validarTokenUsuario(token);
            chain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("[BFF-AUTH] Token inválido ou expirado: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
        }
    }

    private boolean isPublica(String method, String uri) {
        // Login e preflight sempre livres
        if ("OPTIONS".equalsIgnoreCase(method)) return true;
        if (uri.startsWith("/api/auth/"))       return true;
        // Cadastro de nova pessoa é público
        if ("POST".equalsIgnoreCase(method) && uri.equals("/api/persons")) return true;
        return false;
    }
}
