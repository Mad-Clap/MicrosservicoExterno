package com.example.externo.model;

import jakarta.persistence.*;

@Entity
public class ReqEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String assunto;
    @Column(nullable = false)
    private String mensagem;


    public ReqEmail(){}
    public ReqEmail(String email, String assunto, String mensagem) {
        this.email = email;
        this.assunto = assunto;
        this.mensagem = mensagem;
    }



    //setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public void setMensagem(String mensagem) {
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

    public Long getId() {
        return id;
    }


}
