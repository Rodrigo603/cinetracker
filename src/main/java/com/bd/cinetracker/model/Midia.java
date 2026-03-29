package com.bd.cinetracker.model;

import java.util.Objects;

public abstract class Midia {

    private Integer idMidia;
    private String idImdb;
    private String titulo;
    private String descricao;
    private String posterUrl;
    private Integer anoLancamento;
    private String paisOrigem;
    private Double avaliacao;
    private Double notaImdb;
    private Integer fkUsuario;
    private Boolean assistido = false;

    public Midia() {}

    public Midia(Integer idMidia, String idImdb, String titulo, String descricao,
                 String posterUrl, Integer anoLancamento, String paisOrigem, Double avaliacao, Double notaImdb, Integer fkUsuario,Boolean assistido) {

        this.idMidia = idMidia;
        this.idImdb = idImdb;
        this.titulo = titulo;
        this.descricao = descricao;
        this.posterUrl = posterUrl;
        this.anoLancamento = anoLancamento;
        this.paisOrigem = paisOrigem;
        this.avaliacao = avaliacao;
        this.notaImdb = notaImdb;
        this.fkUsuario = fkUsuario;
        this.assistido = assistido;
    }

    public Integer getIdMidia() {
        return idMidia;
    }

    public void setIdMidia(Integer idMidia) {
        this.idMidia = idMidia;
    }

    public String getIdImdb() {
        return idImdb;
    }

    public void setIdImdb(String idImdb) {
        this.idImdb = idImdb;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Double getNotaImdb() {
        return notaImdb;
    }

    public void setNotaImdb(Double notaImdb) {
        this.notaImdb = notaImdb;
    }

    public Integer getFkUsuario() {
        return fkUsuario;
    }

    public void setFkUsuario(Integer fkUsuario) {
        this.fkUsuario = fkUsuario;
    }

    public Boolean getAssistido() {
        return assistido;
    }

    public void setAssistido(Boolean assistido) {
        this.assistido = assistido;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Midia midia = (Midia) o;
        return Objects.equals(getIdMidia(), midia.getIdMidia()) && Objects.equals(getIdImdb(), midia.getIdImdb());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdMidia(), getIdImdb());
    }
}
