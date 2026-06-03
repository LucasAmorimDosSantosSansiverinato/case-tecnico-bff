package com.desafioTecnico.bff.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterPersonRequestTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        RegisterPersonRequest req = new RegisterPersonRequest();
        req.setFullName("John Doe");
        req.setDocument("12345678900");
        req.setEmail("john@example.com");
        req.setBirthDate(LocalDate.of(1990, 1, 1));
        req.setCep("01310100");
        req.setComplement("Apt 1");
        req.setNumber("100");

        assertThat(req.getFullName()).isEqualTo("John Doe");
        assertThat(req.getDocument()).isEqualTo("12345678900");
        assertThat(req.getEmail()).isEqualTo("john@example.com");
        assertThat(req.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(req.getCep()).isEqualTo("01310100");
        assertThat(req.getComplement()).isEqualTo("Apt 1");
        assertThat(req.getNumber()).isEqualTo("100");
    }
}
