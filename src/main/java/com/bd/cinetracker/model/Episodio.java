package com.bd.cinetracker.model;

import java.util.Objects;

public class Episodio {
    private Integer numEpisodio;
    private Integer fkTemporadaId;
    private String titulo;
    private Integer duracao;
    private String descricao;

    public Episodio() {
    }

    public Episodio(Integer numEpisodio, Integer fkTemporadaId, String titulo, Integer duracao, String descricao) {
        this.numEpisodio = numEpisodio;
        this.fkTemporadaId = fkTemporadaId;
        this.titulo = titulo;
        this.duracao = duracao;
        this.descricao = descricao;
    }

    public Integer getNumEpisodio() {
        return numEpisodio;
    }

    public void setNumEpisodio(Integer numEpisodio) {
        this.numEpisodio = numEpisodio;
    }

    public Integer getFkTemporadaId() {
        return fkTemporadaId;
    }

    public void setFkTemporadaId(Integer fkTemporadaId) {
        this.fkTemporadaId = fkTemporadaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Episodio episodio = (Episodio) o;
        return Objects.equals(getNumEpisodio(), episodio.getNumEpisodio()) && Objects.equals(getFkTemporadaId(), episodio.getFkTemporadaId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumEpisodio(), getFkTemporadaId());
    }
}