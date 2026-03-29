package com.bd.cinetracker.model;

import java.util.Objects;

public class Genero {
    private String nomeGenero;

    public Genero() {}

    public Genero(String nomeGenero) {
        this.nomeGenero = nomeGenero;
    }

    public String getNomeGenero() { return nomeGenero; }
    public void setNomeGenero(String nomeGenero) { this.nomeGenero = nomeGenero; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genero genero = (Genero) o;
        return Objects.equals(nomeGenero, genero.nomeGenero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeGenero);
    }
}