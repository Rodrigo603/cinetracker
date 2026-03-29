package com.bd.cinetracker.model.enums;

public enum TipoPessoa {
    ATOR(1),
    DIRETOR(2),
    AMBOS(3);

    private final int valor;

    TipoPessoa(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static TipoPessoa fromInt(int valor) {
        for (TipoPessoa tipo : TipoPessoa.values()) {
            if (tipo.valor == valor) return tipo;
        }
        throw new IllegalArgumentException("Tipo de pessoa inválido: " + valor);
    }
}
