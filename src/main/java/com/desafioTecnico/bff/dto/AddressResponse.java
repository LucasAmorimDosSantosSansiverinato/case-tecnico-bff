package com.desafioTecnico.bff.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressResponse {

    @JsonProperty("cep")
    private String cep;

    @JsonProperty("logradouro")
    private String street;

    @JsonProperty("bairro")
    private String neighborhood;

    @JsonProperty("localidade")
    private String city;

    @JsonProperty("uf")
    private String state;

    @JsonProperty("erro")
    private Boolean erro;

    public AddressResponse() {}

    public String getCep()          { return cep; }
    public String getStreet()       { return street; }
    public String getNeighborhood() { return neighborhood; }
    public String getCity()         { return city; }
    public String getState()        { return state; }
    public Boolean getErro()        { return erro; }
}
