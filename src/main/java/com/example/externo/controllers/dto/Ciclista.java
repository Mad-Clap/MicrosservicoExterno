package com.example.externo.controllers.dto;

import jakarta.validation.constraints.*;

import java.util.Objects;

public class Ciclista {
    public Ciclista() {}

    public Ciclista(Long id, String status, String nome, String nascimento, String cpf,
                    Passaporte passaporte, String nacionalidade, String email, String urlFotoDocumento) {
        this.id = id;
        this.status = status;
        this.nome = nome;
        this.nascimento = nascimento;
        this.cpf = cpf;
        this.passaporte = passaporte;
        this.nacionalidade = nacionalidade;
        this.email = email;
        this.urlFotoDocumento = urlFotoDocumento;
    }

    @NotNull
    @Min(1)
    private Long id;

    @NotNull(message = "Dados não existentes")
    @Pattern(regexp = "ATIVO|INATIVO|AGUARDANDO_CONFIRMACAO",message = "Dados inválidos")
    private String status;

    @NotNull
    @Size(min = 1)
    private String nome;

    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String nascimento;

    @NotNull
    @Pattern(regexp = "\\d{11}")
    private String cpf;

    private Passaporte passaporte;
    @NotNull
    @Pattern(regexp = "BRASILEIRO|ESTRANGEIRO")
    private String nacionalidade;

    @NotNull
    private String email;

    @NotNull
    private String urlFotoDocumento;

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getNascimento() {
        return nascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public String getUrlFotoDocumento() {
        return urlFotoDocumento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ciclista ciclista = (Ciclista) o;
        return id.equals(ciclista.id) && status.equals(ciclista.status) && nome.equals(ciclista.nome) && nascimento.equals(ciclista.nascimento) && cpf.equals(ciclista.cpf) && Objects.equals(passaporte, ciclista.passaporte) && nacionalidade.equals(ciclista.nacionalidade) && email.equals(ciclista.email) && urlFotoDocumento.equals(ciclista.urlFotoDocumento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, nome, nascimento, cpf, passaporte, nacionalidade, email, urlFotoDocumento);
    }
}

