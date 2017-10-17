package com.poliusp.monografia.entity;

public class IndiceNegociabilidade implements Comparable<IndiceNegociabilidade> {

    public IndiceNegociabilidade(String simbolo)
    {
        this.setSimbolo(simbolo);
    }

    private String simbolo;
    private double indiceNegociabilidade;
    private Boolean percenteIndicice = false;
    private String criterioExclusão;

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public double getIndiceNegociabilidade() {
        return indiceNegociabilidade;
    }

    public void setIndiceNegociabilidade(double indiceNegociabilidade) {
        this.indiceNegociabilidade = indiceNegociabilidade;
    }

    public Boolean getPercenteIndicice() {
        return percenteIndicice;
    }

    public void setPercenteIndicice(Boolean percenteIndicice) {
        this.percenteIndicice = percenteIndicice;
    }

    public String getCriterioExclusão() {
        return criterioExclusão;
    }

    public void setCriterioExclusão(String criterioExclusão) {
        this.criterioExclusão = criterioExclusão;
    }

    @Override
    public int compareTo(IndiceNegociabilidade indice) {
        if (this.getIndiceNegociabilidade()<indice.getIndiceNegociabilidade()){
            return 1;
        }else{
            return -1;
        }
    }
}
