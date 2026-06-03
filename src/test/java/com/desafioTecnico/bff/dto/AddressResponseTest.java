package com.desafioTecnico.bff.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserialize_allFields_mapsCorrectly() throws Exception {
        String json = """
                {
                  "cep": "01310-100",
                  "logradouro": "Avenida Paulista",
                  "bairro": "Bela Vista",
                  "localidade": "São Paulo",
                  "uf": "SP",
                  "erro": false
                }
                """;

        AddressResponse response = mapper.readValue(json, AddressResponse.class);

        assertThat(response.getCep()).isEqualTo("01310-100");
        assertThat(response.getStreet()).isEqualTo("Avenida Paulista");
        assertThat(response.getNeighborhood()).isEqualTo("Bela Vista");
        assertThat(response.getCity()).isEqualTo("São Paulo");
        assertThat(response.getState()).isEqualTo("SP");
        assertThat(response.getErro()).isFalse();
    }

    @Test
    void deserialize_erroTrue_flagSet() throws Exception {
        String json = "{\"erro\": true}";

        AddressResponse response = mapper.readValue(json, AddressResponse.class);

        assertThat(response.getErro()).isTrue();
    }

    @Test
    void deserialize_unknownFields_ignored() throws Exception {
        String json = "{\"cep\":\"12345-678\", \"extra\":\"ignored\"}";

        AddressResponse response = mapper.readValue(json, AddressResponse.class);

        assertThat(response.getCep()).isEqualTo("12345-678");
    }

    @Test
    void defaultConstructor_allFieldsNull() {
        AddressResponse response = new AddressResponse();

        assertThat(response.getCep()).isNull();
        assertThat(response.getStreet()).isNull();
        assertThat(response.getNeighborhood()).isNull();
        assertThat(response.getCity()).isNull();
        assertThat(response.getState()).isNull();
        assertThat(response.getErro()).isNull();
    }
}
