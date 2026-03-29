package com.bd.cinetracker.model;

import java.lang.Integer;
import java.util.Objects;

public class Filme extends Midia {

    private Double bilheteria;
    private Integer duracao;

    public Filme() {
        super();
    }

    public Filme(Integer idMidia, String idImdb, String titulo, String descricao, String posterUrl,
                 Double bilheteria, Integer duracao, Integer anoLancamento, String paisOrigem, Double avaliacao,
                 Double notaImdb, Integer fkUsuario, Boolean assistido) {

        super(idMidia,idImdb,titulo,descricao,posterUrl,anoLancamento,paisOrigem,avaliacao,notaImdb,fkUsuario,assistido);
        this.bilheteria = bilheteria;
        this.duracao = duracao;

    }

    public Double getBilheteria() {
        return bilheteria;
    }

    public void setBilheteria(Double bilheteria) {
        this.bilheteria = bilheteria;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }
}
