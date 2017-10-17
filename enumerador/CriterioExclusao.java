package com.poliusp.monografia.enumerador;

import java.util.List;

public final class CriterioExclusao {
    public enum CriterioExclusaoEnum
    {
        PennyStock,
        Abaixo_85IN,
        Abaixo_90IN,
        Presenca_95_Pregao,
        ValorFinanceiroAbaixo
    }

    public static String getDescricao(List<CriterioExclusaoEnum> criterios) {

        String result = "";
        for(CriterioExclusaoEnum criterio: criterios)
        {
            result += ((!result.isEmpty())?";":"") + CriterioExclusao.getDescricao(criterio);
        }

        return result;
    }

    public static String getDescricao(CriterioExclusaoEnum criterio) {

        if ( criterio.equals(CriterioExclusaoEnum.PennyStock)) {
            return "Penny Stock";
        }
        else if ( criterio.equals(CriterioExclusaoEnum.Abaixo_85IN)) {
            return  "Abaixo 85%";
        }
        else if ( criterio.equals(CriterioExclusaoEnum.Abaixo_90IN)) {
            return  "Abaixo 90%";
        }
        else if ( criterio.equals(CriterioExclusaoEnum.Presenca_95_Pregao)) {
            return  "Sem 95% de Pregões";
        }
        else if ( criterio.equals(CriterioExclusaoEnum.ValorFinanceiroAbaixo)) {
            return  "Volume < a 0,1%";
        }
        return null;
    }
}
