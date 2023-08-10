package com.example.externo.controllers.dto;

import java.util.Objects;

public class Passaporte {
    public Passaporte(String numero, String validade, String pais) {
        this.numero = numero;
        this.validade = validade;
        this.pais = pais;
    }

    private String numero;
    private String validade;
    private String pais;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passaporte that = (Passaporte) o;
        return numero.equals(that.numero) && validade.equals(that.validade) && pais.equals(that.pais);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, validade, pais);
    }
}
