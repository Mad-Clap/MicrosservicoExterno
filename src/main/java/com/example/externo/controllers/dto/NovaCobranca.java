package com.example.externo.controllers.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class NovaCobranca {

    @NotNull
    @Min(0)
    private int valor;

    @NotNull
    @Min(1)
    private Long ciclista;

    public NovaCobranca() {

    }

    public NovaCobranca(int valor, Long ciclista) {
        this.valor = valor;
        this.ciclista = ciclista;
    }

    public int getValor() {
        return valor;
    }

    public Long getCiclista() {
        return ciclista;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public void setCiclista(Long ciclista) {
        this.ciclista = ciclista;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovaCobranca that = (NovaCobranca) o;
        return valor == that.valor && ciclista.equals(that.ciclista);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, ciclista);
    }
}
