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

    public String getNomeCompleto()                        { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto)       { this.nomeCompleto = nomeCompleto; }

    public String getCpf()                                 { return cpf; }
    public void setCpf(String cpf)                         { this.cpf = cpf; }

    public String getEmail()                               { return email; }
    public void setEmail(String email)                     { this.email = email; }

    public LocalDate getDataNascimento()                   { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getCep()                                 { return cep; }
    public void setCep(String cep)                         { this.cep = cep; }

    public String getComplemento()                         { return complemento; }
    public void setComplemento(String complemento)         { this.complemento = complemento; }

    public String getNumero()                              { return numero; }
    public void setNumero(String numero)                   { this.numero = numero; }
}
