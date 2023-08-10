package com.example.externo.controllers.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public class NovoEmail {
    @NotNull
    @Pattern(regexp = ".+@.+")
    private String email;
    @NotNull
    private String assunto;
    @NotNull
    private String mensagem;

    public NovoEmail(){}

    public NovoEmail(String email, String assunto, String mensagem) {
        this.email = email;
        this.assunto = assunto;
        this.mensagem = mensagem;
    }

    //getters
    public String getEmail() {
        return email;
    }

    public String getAssunto() {
        return assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    //setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovoEmail novoEmail = (NovoEmail) o;
        return email.equals(novoEmail.email) && assunto.equals(novoEmail.assunto) && mensagem.equals(novoEmail.mensagem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, assunto, mensagem);
    }
}
