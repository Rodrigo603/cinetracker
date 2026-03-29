package com.bd.cinetracker.model;

public class Lista {
    private Integer idLista;
    private String nomeLista;

    public Lista() {}

    public Lista(Integer idLista, String nomeLista) {
        this.idLista = idLista;
        this.nomeLista = nomeLista;
    }

    public Integer getIdLista() {
        return idLista;
    }

    public void setIdLista(Integer idLista) {
        this.idLista = idLista;
    }

    public String getNomeLista() {
        return nomeLista;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }
}
