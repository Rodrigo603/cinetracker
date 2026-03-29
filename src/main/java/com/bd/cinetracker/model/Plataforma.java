package com.bd.cinetracker.model;

import java.util.Objects;

public class Plataforma {
    //CREATE TABLE PLATAFORMA (
    //    ID_PLATAFORMA INT AUTO_INCREMENT PRIMARY KEY,
    //    NOME VARCHAR(100) NOT NULL UNIQUE,
    //    URL VARCHAR(255)

    private Integer idPlataforma;
    private String nome;
    private String url;

    public Plataforma() {}

    public Plataforma(Integer idPlataforma, String nome, String url) {
        this.idPlataforma = idPlataforma;
        this.nome = nome;
        this.url = url;
    }

    public Integer getIdPlataforma() {
        return idPlataforma;
    }

    public void setIdPlataforma(Integer idPlataforma) {
        this.idPlataforma = idPlataforma;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Plataforma that = (Plataforma) o;
        return Objects.equals(getIdPlataforma(), that.getIdPlataforma());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdPlataforma());
    }
}
