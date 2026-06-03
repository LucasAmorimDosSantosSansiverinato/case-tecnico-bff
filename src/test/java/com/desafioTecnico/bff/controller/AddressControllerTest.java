package com.desafioTecnico.bff.controller;

import com.desafioTecnico.bff.dto.AddressResponse;
import com.desafioTecnico.bff.service.BackendProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    // BackendProxyService must be mocked even if unused to satisfy Spring context
    @MockBean
    private BackendProxyService proxy;

    private AddressResponse validAddress() {
        AddressResponse r = new AddressResponse();
        // Use reflection-free approach: Jackson will deserialize, but here we build via setter-less DTO.
        // The class has no setters, so we rely on the mock returning a pre-populated instance via a subclass.
        return r;
    }

    @Test
    void findByCep_validCep_returns200WithNormalizedMap() throws Exception {
        AddressResponse address = buildAddress(false, "Rua Paulista", "Centro", "São Paulo", "SP");

        when(restTemplate.getForObject(
                eq("https://viacep.com.br/ws/{cep}/json/"),
                eq(AddressResponse.class),
                eq("01310100")))
                .thenReturn(address);

        mockMvc.perform(get("/api/address/01310100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01310100"))
                .andExpect(jsonPath("$.street").value("Rua Paulista"))
                .andExpect(jsonPath("$.neighborhood").value("Centro"))
                .andExpect(jsonPath("$.city").value("São Paulo"))
                .andExpect(jsonPath("$.state").value("SP"));
    }

    @Test
    void findByCep_withFormatting_stripsNonDigits() throws Exception {
        AddressResponse address = buildAddress(false, "Rua X", "Bairro", "Cidade", "UF");

        when(restTemplate.getForObject(
                eq("https://viacep.com.br/ws/{cep}/json/"),
                eq(AddressResponse.class),
                eq("01310100")))
                .thenReturn(address);

        mockMvc.perform(get("/api/address/01310-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01310100"));
    }

    @Test
    void findByCep_lessThan8Digits_returns400() throws Exception {
        mockMvc.perform(get("/api/address/1234567"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("CEP must have 8 digits"));
    }

    @Test
    void findByCep_moreThan8Digits_returns400() throws Exception {
        mockMvc.perform(get("/api/address/012345678"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("CEP must have 8 digits"));
    }

    @Test
    void findByCep_alphaOnly_returns400() throws Exception {
        mockMvc.perform(get("/api/address/abcdefgh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByCep_viaCepReturnsErroTrue_returns404() throws Exception {
        AddressResponse notFound = buildAddress(true, null, null, null, null);

        when(restTemplate.getForObject(
                eq("https://viacep.com.br/ws/{cep}/json/"),
                eq(AddressResponse.class),
                eq("00000000")))
                .thenReturn(notFound);

        mockMvc.perform(get("/api/address/00000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("CEP not found: 00000000"));
    }

    @Test
    void findByCep_viaCepReturnsNull_returns404() throws Exception {
        when(restTemplate.getForObject(
                eq("https://viacep.com.br/ws/{cep}/json/"),
                eq(AddressResponse.class),
                eq("00000001")))
                .thenReturn(null);

        mockMvc.perform(get("/api/address/00000001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByCep_nullFields_returnsEmptyStrings() throws Exception {
        AddressResponse address = buildAddress(false, null, null, null, null);

        when(restTemplate.getForObject(
                eq("https://viacep.com.br/ws/{cep}/json/"),
                eq(AddressResponse.class),
                eq("12345678")))
                .thenReturn(address);

        mockMvc.perform(get("/api/address/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value(""))
                .andExpect(jsonPath("$.neighborhood").value(""))
                .andExpect(jsonPath("$.city").value(""))
                .andExpect(jsonPath("$.state").value(""));
    }

    /**
     * Builds an AddressResponse using Jackson deserialization to work around the lack of setters.
     */
    private AddressResponse buildAddress(Boolean erro, String street, String neighborhood, String city, String state) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            StringBuilder json = new StringBuilder("{");
            if (erro != null)        json.append("\"erro\":").append(erro).append(",");
            if (street != null)      json.append("\"logradouro\":\"").append(street).append("\",");
            if (neighborhood != null) json.append("\"bairro\":\"").append(neighborhood).append("\",");
            if (city != null)        json.append("\"localidade\":\"").append(city).append("\",");
            if (state != null)       json.append("\"uf\":\"").append(state).append("\",");
            // remove trailing comma
            String s = json.toString();
            if (s.endsWith(",")) s = s.substring(0, s.length() - 1);
            s += "}";
            return om.readValue(s, AddressResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
