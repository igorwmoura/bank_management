package org.example;

import java.io.Serializable;

public class Transacao implements Serializable {
    private final String tipoTransacao;
    private final int valorTransacao;
    private final String dataTransacao;

    public Transacao(String tipoTransacao, int valorTransacao, String dataTransacao) {
        this.tipoTransacao = tipoTransacao;
        this.valorTransacao = valorTransacao;
        this.dataTransacao = dataTransacao;
    }

    public String getTipoTransacao() {
        return tipoTransacao;
    }

    public int getValorTransacao() {
        return valorTransacao;
    }

    public String getDataTransacao() {
        return dataTransacao;
    }
}