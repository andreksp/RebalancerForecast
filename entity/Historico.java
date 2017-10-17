package com.poliusp.monografia.entity;

import java.util.Date;
import java.util.List;

public class Historico {
    private String simbolo;
    private Date dataPregao;
    private double valor;
    private int mercado;
    private long quantidadeNegociada;
    private int idAcao;

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getMercado() {
        return mercado;
    }

    public void setMercado(int mercado) {
        this.mercado = mercado;
    }

    public long getQuantidadeNegociada() {
        return quantidadeNegociada;
    }

    public void setQuantidadeNegociada(long quantidadeNegociada) {
        this.quantidadeNegociada = quantidadeNegociada;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public Date getDataPregao() {
        return dataPregao;
    }

    public void setDataPregao(Date dataPregao) {
        this.dataPregao = dataPregao;
    }

    public int getIdAcao() {
        return idAcao;
    }

    public void setIdAcao(int idAcao) {
        this.idAcao = idAcao;
    }

    public double getValume()
    {
        return this.getQuantidadeNegociada() * this.getValor();
    }

}
