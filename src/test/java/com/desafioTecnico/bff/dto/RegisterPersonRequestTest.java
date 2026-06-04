package com.desafioTecnico.bff.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterPersonRequestTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        RegisterPersonRequest req = new RegisterPersonRequest();
        req.setNomeCompleto("João Silva");
        req.setCpf("12345678900");
        req.setEmail("joao@email.com");
        req.setDataNascimento(LocalDate.of(1990, 1, 1));
        req.setCep("01310100");
        req.setComplemento("Apto 1");
        req.setNumero("100");

        assertThat(req.getNomeCompleto()).isEqualTo("João Silva");
        assertThat(req.getCpf()).isEqualTo("12345678900");
        assertThat(req.getEmail()).isEqualTo("joao@email.com");
        assertThat(req.getDataNascimento()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(req.getCep()).isEqualTo("01310100");
        assertThat(req.getComplemento()).isEqualTo("Apto 1");
        assertThat(req.getNumero()).isEqualTo("100");
    }
}
