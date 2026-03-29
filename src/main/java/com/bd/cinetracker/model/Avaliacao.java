package com.bd.cinetracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Avaliacao {

    private Integer idAvaliacao;
    private Integer fkUsuarioId;
    private Integer fkFilmeId;
    private Integer fkSerieId;
    private Double nota;
    private String comentario;
    private LocalDateTime dataAvaliacao;

    public Avaliacao() {}

    public Avaliacao(Integer idAvaliacao, Integer fkUsuarioId, Integer fkFilmeId,
                     Integer fkSerieId, Double nota, String comentario, LocalDateTime dataAvaliacao) {

        this.idAvaliacao = idAvaliacao;
        this.fkUsuarioId = fkUsuarioId;
        this.fkFilmeId = fkFilmeId;
        this.fkSerieId = fkSerieId;
        this.nota = nota;
        this.comentario = comentario;
        this.dataAvaliacao = dataAvaliacao;
    }

    public Integer getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(Integer idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public Integer getFkUsuarioId() {
        return fkUsuarioId;
    }

    public void setFkUsuarioId(Integer fkUsuarioId) {
        this.fkUsuarioId = fkUsuarioId;
    }

    public Integer getFkFilmeId() {
        return fkFilmeId;
    }

    public void setFkFilmeId(Integer fkFilmeId) {
        this.fkFilmeId = fkFilmeId;
    }

    public Integer getFkSerieId() {
        return fkSerieId;
    }

    public void setFkSerieId(Integer fkSerieId) {
        this.fkSerieId = fkSerieId;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return Objects.equals(getIdAvaliacao(), avaliacao.getIdAvaliacao());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdAvaliacao());
    }
}
