package com.desafioTecnico.bff.controller;

import com.desafioTecnico.bff.dto.AddressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final RestTemplate restTemplate;

    public AddressController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<?> findByCep(@PathVariable String cep) {
        String digits = cep.replaceAll("[^0-9]", "");
        if (digits.length() != 8) {
            return ResponseEntity.badRequest().body(Map.of("detail", "CEP must have 8 digits"));
        }

        AddressResponse response = restTemplate.getForObject(
                "https://viacep.com.br/ws/{cep}/json/",
                AddressResponse.class,
                digits
        );

        if (response == null || Boolean.TRUE.equals(response.getErro())) {
            return ResponseEntity.status(404).body(Map.of("detail", "CEP not found: " + digits));
        }

        return ResponseEntity.ok(Map.of(
                "cep",          digits,
                "street",       response.getStreet()       != null ? response.getStreet()       : "",
                "neighborhood", response.getNeighborhood() != null ? response.getNeighborhood() : "",
                "city",         response.getCity()         != null ? response.getCity()         : "",
                "state",        response.getState()        != null ? response.getState()        : ""
        ));
    }
}
