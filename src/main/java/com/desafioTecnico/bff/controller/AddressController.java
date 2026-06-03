package com.desafioTecnico.bff.controller;

import com.desafioTecnico.bff.dto.AddressResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    private final RestTemplate restTemplate;

    public AddressController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<?> findByCep(@PathVariable String cep) {
        String digits = cep.replaceAll("[^0-9]", "");
        log.info("[BFF] Buscando CEP: {}", digits);

        if (digits.length() != 8) {
            log.warn("[BFF] CEP inválido: {} (não tem 8 dígitos)", digits);
            return ResponseEntity.badRequest().body(Map.of("detail", "CEP must have 8 digits"));
        }

        AddressResponse response = restTemplate.getForObject(
                "https://viacep.com.br/ws/{cep}/json/",
                AddressResponse.class,
                digits
        );

        if (response == null || Boolean.TRUE.equals(response.getErro())) {
            log.warn("[BFF] CEP não encontrado: {}", digits);
            return ResponseEntity.status(404).body(Map.of("detail", "CEP not found: " + digits));
        }

        log.info("[BFF] CEP {} encontrado: {} - {}/{}", digits, response.getStreet(), response.getCity(), response.getState());
        return ResponseEntity.ok(Map.of(
                "cep",          digits,
                "street",       response.getStreet()       != null ? response.getStreet()       : "",
                "neighborhood", response.getNeighborhood() != null ? response.getNeighborhood() : "",
                "city",         response.getCity()         != null ? response.getCity()         : "",
                "state",        response.getState()        != null ? response.getState()        : ""
        ));
    }
}
