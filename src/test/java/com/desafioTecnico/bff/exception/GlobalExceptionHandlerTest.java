package com.desafioTecnico.bff.exception;

import com.desafioTecnico.bff.controller.PersonController;
import com.desafioTecnico.bff.service.BackendProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BackendProxyService proxy;

    @Test
    void validationError_returns400WithFieldErrors() throws Exception {
        // Missing all required fields triggers MethodArgumentNotValidException
        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    void genericException_returns500() throws Exception {
        when(proxy.post(eq("/api/v1/persons"), any()))
                .thenThrow(new RuntimeException("unexpected"));

        String json = """
                {
                  "fullName": "John Doe",
                  "document": "12345678900",
                  "email": "john@example.com",
                  "birthDate": "1990-01-01",
                  "cep": "01310100"
                }
                """;

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"));
    }
}
