package com.desafioTecnico.bff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public class RegisterPersonRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    private String document;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotBlank
    private String cep;

    private String complement;
    private String number;

    public RegisterPersonRequest() {}

    public String getFullName()            { return fullName; }
    public void setFullName(String v)      { this.fullName = v; }

    public String getDocument()            { return document; }
    public void setDocument(String v)      { this.document = v; }

    public String getEmail()               { return email; }
    public void setEmail(String v)         { this.email = v; }

    public LocalDate getBirthDate()        { return birthDate; }
    public void setBirthDate(LocalDate v)  { this.birthDate = v; }

    public String getCep()                 { return cep; }
    public void setCep(String v)           { this.cep = v; }

    public String getComplement()          { return complement; }
    public void setComplement(String v)    { this.complement = v; }

    public String getNumber()              { return number; }
    public void setNumber(String v)        { this.number = v; }
}
