package com.example.externo.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Cobranca implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private Status status;
    @Column(nullable = false)
    private String horaSolicitacao;
    @Column(nullable = false)
    private String horaFinalizacao;

    @Column(nullable = false)
    private int valor;

    @Column(nullable = false)
    private Long ciclista;

    public Cobranca() {}

    public Cobranca( Status status, String horaSolicitacao, String horaFinalizacao, int valor, Long ciclista) {
        this.status = status;
        this.horaSolicitacao = horaSolicitacao;
        this.horaFinalizacao = horaFinalizacao;
        this.valor =valor;
        this.ciclista = ciclista;
    }

    //setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setHoraSolicitacao(String horaSolicitacao) {
        this.horaSolicitacao = horaSolicitacao;
    }

    public void setHoraFinalizacao(String horaFinalizacao) {
        this.horaFinalizacao = horaFinalizacao;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public void setCiclista(Long ciclista) {
        this.ciclista = ciclista;
    }

    //getters
    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public String getHoraSolicitacao() {
        return horaSolicitacao;
    }

    public String getHoraFinalizacao() {
        return horaFinalizacao;
    }

    public int getValor() {
        return valor;
    }

    public Long getCiclista() {
        return ciclista;
    }
}

