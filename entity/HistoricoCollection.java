package com.poliusp.monografia.entity;

import java.util.ArrayList;
import java.util.List;

public class HistoricoCollection extends ArrayList<Historico>
{
    private double indiceNegociabilidade;

    public double getIndiceNegociabilidade() {
        return indiceNegociabilidade;
    }

    public void setIndiceNegociabilidade(double indiceNegociabilidade) {
        this.indiceNegociabilidade = indiceNegociabilidade;
    }

    public double getMediaPonderadaValorAcao()
    {
        List<Historico> listaSemUltimoValor = new ArrayList<Historico>();

        //Removendo o último item da lista.
        for(int i =0; i < this.size() - 2; i++)
        {
            listaSemUltimoValor.add(this.get(i));
        }

        //Calculando a média ponderada sem o último item da lista.
        return listaSemUltimoValor.stream().mapToDouble(p -> p.getQuantidadeNegociada() * p.getValor()).sum() / listaSemUltimoValor.stream().mapToDouble(p -> p.getQuantidadeNegociada()).sum();
    }

    public int getSize()
    {
        return this.size();
    }

    public double getVolumeFinanceiro()
    {
        List<Historico> listaSemUltimoValor = new ArrayList<Historico>();

        //Removendo o último item da lista.
        for(int i =0; i < this.size() - 2; i++)
        {
            listaSemUltimoValor.add(this.get(i));
        }

        //Calculando o volume sem o último item da lista.
        return listaSemUltimoValor.stream().mapToDouble(p -> p.getQuantidadeNegociada() * p.getValor()).sum();
    }
}

