package com.example.externo.controllers.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class NovoCartaoDeCredito {

    @NotNull
    @Pattern(regexp = "\\d{3,4}")
    private String cvv;

    @NotNull
    @Size(min=1)
    private String nomeTitular;

    @NotNull
    @Pattern(regexp = "\\d+")
    private String numero;

    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String validade;

    @Min(1)
    private double id=1;

    @Min(1)
    private double idCiclista=1;

    public NovoCartaoDeCredito() {
    }

    public NovoCartaoDeCredito(String cvv, String nomeTitular, String numero, String validade) {
        this.cvv = cvv;
        this.nomeTitular = nomeTitular;
        this.numero = numero;
        this.validade = validade;
    }

    public NovoCartaoDeCredito(String cvv, String nomeTitular, String numero, String validade, double id,
                               double idCiclista) {
        this.cvv = cvv;
        this.nomeTitular = nomeTitular;
        this.numero = numero;
        this.validade = validade;
        this.id=id;
        this.idCiclista=idCiclista;
    }

    //getters
    public String getCvv() {
        return cvv;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public String getNumero() {
        return numero;
    }

    public String getValidade() {
        return validade;
    }

    //setters

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovoCartaoDeCredito that = (NovoCartaoDeCredito) o;
        return Double.compare(that.id, id) == 0 && Double.compare(that.idCiclista, idCiclista) == 0 && cvv.equals(that.cvv) && nomeTitular.equals(that.nomeTitular) && numero.equals(that.numero) && validade.equals(that.validade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cvv, nomeTitular, numero, validade, id, idCiclista);
    }
}
