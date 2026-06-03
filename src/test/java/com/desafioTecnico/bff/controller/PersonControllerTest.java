package com.desafioTecnico.bff.controller;

import com.desafioTecnico.bff.service.BackendProxyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BackendProxyService proxy;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private String validRequestJson() throws Exception {
        return mapper.writeValueAsString(Map.of(
                "fullName", "John Doe",
                "document", "12345678900",
                "email", "john@example.com",
                "birthDate", "1990-01-01",
                "cep", "01310100"
        ));
    }

    @Test
    void register_happyPath_returns201() throws Exception {
        when(proxy.post(eq("/api/v1/persons"), any()))
                .thenReturn(ResponseEntity.status(201).body(Map.of("id", "abc")));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated());
    }

    @Test
    void register_missingFullName_returns400() throws Exception {
        String json = mapper.writeValueAsString(Map.of(
                "document", "12345678900",
                "email", "john@example.com",
                "birthDate", "1990-01-01",
                "cep", "01310100"
        ));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        String json = mapper.writeValueAsString(Map.of(
                "fullName", "John Doe",
                "document", "12345678900",
                "email", "not-an-email",
                "birthDate", "1990-01-01",
                "cep", "01310100"
        ));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_futureBirthDate_returns400() throws Exception {
        String json = mapper.writeValueAsString(Map.of(
                "fullName", "John Doe",
                "document", "12345678900",
                "email", "john@example.com",
                "birthDate", LocalDate.now().plusYears(1).toString(),
                "cep", "01310100"
        ));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_proxySends422_returns422() throws Exception {
        when(proxy.post(eq("/api/v1/persons"), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("error"));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void register_missingCep_returns400() throws Exception {
        String json = mapper.writeValueAsString(Map.of(
                "fullName", "John Doe",
                "document", "12345678900",
                "email", "john@example.com",
                "birthDate", "1990-01-01"
        ));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_happyPath_returns200() throws Exception {
        when(proxy.get("/api/v1/persons"))
                .thenReturn(ResponseEntity.ok(Map.of("data", "list")));

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_proxySends500_relays500() throws Exception {
        when(proxy.get("/api/v1/persons"))
                .thenReturn(ResponseEntity.status(500).body("error"));

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getById_happyPath_returns200() throws Exception {
        when(proxy.get("/api/v1/persons/42"))
                .thenReturn(ResponseEntity.ok(Map.of("id", "42")));

        mockMvc.perform(get("/api/persons/42"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_proxySends404_relays404() throws Exception {
        when(proxy.get("/api/v1/persons/999"))
                .thenReturn(ResponseEntity.status(404).body("not found"));

        mockMvc.perform(get("/api/persons/999"))
                .andExpect(status().isNotFound());
    }
}
