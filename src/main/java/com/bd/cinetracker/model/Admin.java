package com.bd.cinetracker.model;

import java.time.LocalDate;

public class Admin {
    private Integer idAdmin;
    private String nome;
    private String email;
    private String senha;
    private LocalDate dataCadastro;
    private Integer fkTelefone;

    public Admin() {}

    public Admin(Integer idAdmin, String nome, String email, String senha, LocalDate dataCadastro, Integer fkTelefone) {
        this.idAdmin = idAdmin;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
        this.fkTelefone = fkTelefone;
    }

    public Integer getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Integer idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Integer getFkTelefone() {
        return fkTelefone;
    }

    public void setFkTelefone(Integer fkTelefone) {
        this.fkTelefone = fkTelefone;
    }
}