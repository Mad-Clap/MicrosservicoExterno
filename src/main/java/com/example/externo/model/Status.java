package com.example.externo.model;

public enum Status {
    PENDENTE("PENDENTE"), PAGA("PAGA"), FALHA("FALHA"), CANCELADA("CANCELADA"), OCUPADA("OCUPADA");

    private String dbStatus;

    Status() {
    }

    Status(String dbStatus) {
        this.dbStatus = dbStatus;
    }

    public String getDbStatus() {
        return dbStatus;
    }
}
