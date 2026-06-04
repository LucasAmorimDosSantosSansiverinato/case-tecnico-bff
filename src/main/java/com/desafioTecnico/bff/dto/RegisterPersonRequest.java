package com.desafioTecnico.bff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public class RegisterPersonRequest {

    @NotBlank
    private String nomeCompleto;

    @NotBlank
    private String cpf;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Past
    private LocalDate dataNascimento;

    @NotBlank
    private String cep;

    private String complemento;
    private String numero;

    public RegisterPersonRequest() {}

    public String getNomeCompleto()            { return nomeCompleto; }
    public void setNomeCompleto(String v)      { this.nomeCompleto = v; }

    public String getCpf()                     { return cpf; }
    public void setCpf(String v)               { this.cpf = v; }

    public String getEmail()                   { return email; }
    public void setEmail(String v)             { this.email = v; }

    public LocalDate getDataNascimento()       { return dataNascimento; }
    public void setDataNascimento(LocalDate v) { this.dataNascimento = v; }

    public String getCep()                     { return cep; }
    public void setCep(String v)               { this.cep = v; }

    public String getComplemento()             { return complemento; }
    public void setComplemento(String v)       { this.complemento = v; }

    public String getNumero()                  { return numero; }
    public void setNumero(String v)            { this.numero = v; }
}
