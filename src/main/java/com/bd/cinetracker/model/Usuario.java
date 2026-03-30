package com.bd.cinetracker.model;


import java.time.LocalDate;
import java.util.Objects;

public class Usuario {
    private Integer id;
    private String nome;
    private String email;
    private String senha;
    private LocalDate dataCadastro;
    private Integer fkTelefone;
    private String telefone;

    public Usuario() {}


    public Usuario(Integer id, String nome, String email, String senha, LocalDate dataCadastro, Integer fkTelefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
        this.fkTelefone = fkTelefone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(getId(), usuario.getId()) && Objects.equals(getEmail(), usuario.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail());
    }
}


