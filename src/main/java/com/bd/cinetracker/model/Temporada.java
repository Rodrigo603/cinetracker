package com.bd.cinetracker.model;

public class Temporada {
    private Integer idTemporada;
    private Integer fkSerieId;

    public Temporada() {}

    public Temporada(Integer idTemporada, Integer fkSerieId) {
        this.idTemporada = idTemporada;
        this.fkSerieId = fkSerieId;
    }

    public Integer getIdTemporada() {
        return idTemporada;
    }

    public void setIdTemporada(Integer idTemporada) {
        this.idTemporada = idTemporada;
    }

    public Integer getFkSerieId() {
        return fkSerieId;
    }

    public void setFkSerieId(Integer fkSerieId) {
        this.fkSerieId = fkSerieId;
    }
}
