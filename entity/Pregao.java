package com.poliusp.monografia.entity;
import java.util.Date;

public class Pregao {
    private long quantidadeTotal;
    private double volumeTotal;
    private Date dataPregao;

    public long getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(long quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public double getVolumeTotal() {
        return volumeTotal;
    }

    public void setVolumeTotal(double volumeTotal) {
        this.volumeTotal  = volumeTotal;
    }

    public Date getDataPregao() {
        return dataPregao;
    }

    public void setDataPregao(Date dataPregao) {
        this.dataPregao = dataPregao;
    }
}
