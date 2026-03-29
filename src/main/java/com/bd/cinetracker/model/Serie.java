package com.bd.cinetracker.model;

public class Serie extends Midia {
    private Integer qtdTemporadas;

    public Serie() {}

    public Serie(Integer idMidia, String idImdb, String titulo, String descricao, String posterUrl, Integer anoLancamento,
                 String paisOrigem, Double avaliacao, Double notaImdb, Integer fkUsuario, Integer qtdTemporadas,Boolean assistido) {

        super(idMidia, idImdb, titulo, descricao, posterUrl, anoLancamento, paisOrigem, avaliacao, notaImdb, fkUsuario,assistido);
        this.qtdTemporadas = qtdTemporadas;
    }

    public Integer getQtdTemporadas() {
        return qtdTemporadas;
    }

    public void setQtdTemporadas(Integer qtdTemporadas) {
        this.qtdTemporadas = qtdTemporadas;
    }
}
