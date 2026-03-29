package com.bd.cinetracker.model;

import com.bd.cinetracker.model.enums.TipoPessoa;

import java.util.Objects;

public class Pessoa {

    private Integer idPessoa;
    private String nome;
    private TipoPessoa tipo;

    public Pessoa() {}

    public Pessoa(Integer idPessoa, String nome, TipoPessoa tipo) {
        this.idPessoa = idPessoa;
        this.nome = nome;
        this.tipo = tipo;
    }

    public Integer getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Integer idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoPessoa getTipo() {
        return tipo;
    }

    public void setTipo(TipoPessoa tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return Objects.equals(getIdPessoa(), pessoa.getIdPessoa());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdPessoa());
    }
}
