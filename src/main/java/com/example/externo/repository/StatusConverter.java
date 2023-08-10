package com.example.externo.repository;

import com.example.externo.model.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status,String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getDbStatus();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        switch (dbData) {
            case "PENDENTE":
                return Status.PENDENTE;

            case "PAGA":
                return Status.PAGA;

            case "FALHA":
                return Status.FALHA;

            case "CANCELADA":
                return Status.CANCELADA;
            case "OCUPADA":
                return Status.OCUPADA;
            default:
                throw new IllegalArgumentException("ShortName [" + dbData
                        + "] not supported.");
        }
    }

}
