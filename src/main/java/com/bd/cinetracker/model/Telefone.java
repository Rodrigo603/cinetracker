package com.bd.cinetracker.model;

import java.util.Objects;

public class Telefone {
    private Integer pkTelefone;
    private String telefone;

    public Telefone() {}

    public Telefone(Integer pkTelefone, String telefone) {
        this.pkTelefone = pkTelefone;
        this.telefone = telefone;
    }

    public Integer getPkTelefone() {
        return pkTelefone;
    }

    public void setPkTelefone(Integer pkTelefone) {
        this.pkTelefone = pkTelefone;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Telefone telefone1 = (Telefone) o;
        return Objects.equals(pkTelefone, telefone1.pkTelefone) &&
                Objects.equals(telefone, telefone1.telefone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkTelefone, telefone);
    }
}