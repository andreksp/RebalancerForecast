package com.poliusp.monografia.business;

import com.poliusp.monografia.entity.Historico;
import com.poliusp.monografia.entity.HistoricoCollection;
import com.poliusp.monografia.entity.IndiceNegociabilidade;
import com.poliusp.monografia.entity.Pregao;
import com.poliusp.monografia.enumerador.CriterioExclusao;

import java.util.*;

public class BLCalculo {
    public List<IndiceNegociabilidade> CalcularCarteira(Date dtInicio, Date dtFim, int diasPrevisao) throws Exception {

        BLHistorico blHistorico = new BLHistorico();

        //Pegar todo os histórico do período
        Map<String, HistoricoCollection> historicos = blHistorico.ObterHistorico(dtInicio, dtFim);

        BLPrevisao blPrevisao = new BLPrevisao();
        //Prever as ações
        for (Map.Entry<String, HistoricoCollection> entry : historicos.entrySet()) {
            //não é possível prever só com um valor. também não atende o critério de pregões
            if (entry.getValue().size() > 1) {
                String simbolo = entry.getKey();

                blPrevisao.PreverAcao(dtInicio, dtFim, simbolo, historicos, diasPrevisao);
            }
        }

        historicos.remove("BBTG11");

        //Calcular os totais de cada pregão
        Map<Date, Pregao> totalPorPregao = new HashMap<Date, Pregao>();

        //Identificar os penny stocks. Ação com Média ponderada abaixo de 1 real será removido
        Map<String, List<CriterioExclusao.CriterioExclusaoEnum>> criteriosExclusao = new HashMap<String, List<CriterioExclusao.CriterioExclusaoEnum>>();
        CheckPennyStock(historicos, criteriosExclusao);

        Iterator<Map.Entry<String, List<CriterioExclusao.CriterioExclusaoEnum>>> iter = criteriosExclusao.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, List<CriterioExclusao.CriterioExclusaoEnum>> entry = iter.next();

            historicos.remove(entry.getKey());
        }

        CalcularTotalPregao(historicos, totalPorPregao);

        double totalIndiceNegociabilidade = 0;

        //Calcular o indíce de negociabilidade partical
        for (Map.Entry<String, HistoricoCollection> entry : historicos.entrySet()) {
            HistoricoCollection historicoCollection = entry.getValue();

            for (Historico historico : historicoCollection) {
                Pregao pregao = totalPorPregao.get(historico.getDataPregao());

                //Ainda falta dividir pelo número de pregões.
                double indiceNegociabilidade = Math.cbrt(((double) historico.getQuantidadeNegociada() / pregao.getQuantidadeTotal()) * (Math.pow(((double) historico.getValume() / pregao.getVolumeTotal()), 2)));

                double indNeg = historicoCollection.getIndiceNegociabilidade();
                historicoCollection.setIndiceNegociabilidade(indNeg + indiceNegociabilidade);
            }

            historicoCollection.setIndiceNegociabilidade(historicoCollection.getIndiceNegociabilidade() / entry.getValue().size());

            totalIndiceNegociabilidade += historicoCollection.getIndiceNegociabilidade();
        }

        List<IndiceNegociabilidade> indices = new ArrayList<IndiceNegociabilidade>();

        //Dividir os índices de negociabilidade pelo número de pregões.
        for (Map.Entry<String, HistoricoCollection> entry : historicos.entrySet()) {
            HistoricoCollection historicoCollection = entry.getValue();

            String simbolo = entry.getKey();
            historicoCollection.setIndiceNegociabilidade(historicoCollection.getIndiceNegociabilidade() / totalIndiceNegociabilidade);

            IndiceNegociabilidade in = new IndiceNegociabilidade(simbolo);
            in.setIndiceNegociabilidade(historicoCollection.getIndiceNegociabilidade());
            indices.add(in);
        }

        CheckCriterioPregao(historicos, criteriosExclusao);
        CheckVolumeFinanceiro(historicos, criteriosExclusao);
        CheckIndiceNegociabilidade(indices, criteriosExclusao);

        for(Map.Entry<String, List<CriterioExclusao.CriterioExclusaoEnum>> criterios: criteriosExclusao.entrySet())
        {
            if (criterios.getValue().size() > 1 || criterios.getValue().contains(CriterioExclusao.CriterioExclusaoEnum.PennyStock))
            {
                boolean idExists = indices.stream().anyMatch(p -> p.getSimbolo().equals(criterios.getKey()));

                if ( idExists) {
                    IndiceNegociabilidade indice = indices.stream().filter(p -> p.getSimbolo().equals(criterios.getKey())).findFirst().get();
                    indice.setPercenteIndicice(false);
                    indice.setCriterioExclusão(CriterioExclusao.getDescricao(criterios.getValue()));
                }
                else
                {
                    IndiceNegociabilidade index = new IndiceNegociabilidade(criterios.getKey());
                    index.setCriterioExclusão(CriterioExclusao.getDescricao(criterios.getValue()));
                    index.setPercenteIndicice(false);
                    indices.add(index);
                }
            }
        }
        //Ordenar por Sim/Nao e simbolo.
        Collections.sort(indices, new Comparator<IndiceNegociabilidade>(){
            public int compare(IndiceNegociabilidade o1, IndiceNegociabilidade  o2){
                int sComp = o2.getPercenteIndicice().compareTo(o1.getPercenteIndicice());

                if (sComp != 0) {
                    return sComp;
                } else {
                    return o1.getSimbolo().compareTo(o2.getSimbolo());
                }
            }
        });
        return indices;
    }


    private void CalcularTotalPregao(Map<String, HistoricoCollection> historicos, Map<Date, Pregao> totalPorPregao) {
        for (Map.Entry<String, HistoricoCollection> entry : historicos.entrySet()) {
            HistoricoCollection historicoCollection = entry.getValue();

            for (Historico historico : historicoCollection) {
                if (!totalPorPregao.containsKey(historico.getDataPregao())) {
                    Pregao p = new Pregao();
                    p.setDataPregao(historico.getDataPregao());
                    totalPorPregao.put(historico.getDataPregao(), p);
                }

                Pregao pregao = totalPorPregao.get(historico.getDataPregao());

                long quantidade = pregao.getQuantidadeTotal();
                double volume = pregao.getVolumeTotal();

                pregao.setQuantidadeTotal(quantidade + historico.getQuantidadeNegociada());
                pregao.setVolumeTotal(volume + historico.getValume());
            }
        }
    }

    private void CheckPennyStock(Map<String, HistoricoCollection> historicos, Map<String, List<CriterioExclusao.CriterioExclusaoEnum>> criteriosExclusao) {

        Iterator<Map.Entry<String, HistoricoCollection>> iter = historicos.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, HistoricoCollection> entry = iter.next();
            HistoricoCollection historicoCollection = entry.getValue();

            if (historicoCollection.getMediaPonderadaValorAcao() < 1) {
                if (!criteriosExclusao.containsKey(entry.getKey())) {
                    criteriosExclusao.put(entry.getKey(), new ArrayList<CriterioExclusao.CriterioExclusaoEnum>());
                }
                if (!criteriosExclusao.get(entry.getKey()).contains(CriterioExclusao.CriterioExclusaoEnum.PennyStock)) {
                    criteriosExclusao.get(entry.getKey()).add(CriterioExclusao.CriterioExclusaoEnum.PennyStock);
                }
            }
        }
    }

    private void CheckCriterioPregao(Map<String, HistoricoCollection> historicos, Map<String, List<CriterioExclusao.CriterioExclusaoEnum>> criteriosExclusao) {
        int maxPregooes = 0;
        for (Map.Entry<String, HistoricoCollection> entry : historicos.entrySet()) {
            HistoricoCollection historicoCollection = entry.getValue();
            if ( entry.getValue().getSize() > maxPregooes) {
                maxPregooes = entry.getValue().getSize();
            }
        }

        for (Map.Entry<String, HistoricoCollection> entry : historicos.entrySet()) {
            HistoricoCollection historicoCollection = entry.getValue();

            //Não tém 95% de pregoes
            if ( ((double)historicoCollection.getSize() / maxPregooes) < 0.95) {
                if (!criteriosExclusao.containsKey(entry.getKey())) {
                    criteriosExclusao.put(entry.getKey(), new ArrayList<CriterioExclusao.CriterioExclusaoEnum>());
                }
                if (!criteriosExclusao.get(entry.getKey()).contains(CriterioExclusao.CriterioExclusaoEnum.Presenca_95_Pregao)) {
                    criteriosExclusao.get(entry.getKey()).add(CriterioExclusao.CriterioExclusaoEnum.Presenca_95_Pregao);
                }
            }
        }
    }


    private void CheckVolumeFinanceiro(Map<String, HistoricoCollection> historicos, Map<String, List<CriterioExclusao.CriterioExclusaoEnum>> criteriosExclusao) {
        double volumeFinanceiro = 0;
        for (Map.Entry<String, HistoricoCollection> entry : historicos.entrySet()) {
            HistoricoCollection historicoCollection = entry.getValue();
            volumeFinanceiro += entry.getValue().getVolumeFinanceiro();
        }

        for (Map.Entry<String, HistoricoCollection> entry : historicos.entrySet()) {
            HistoricoCollection historicoCollection = entry.getValue();

            //Não tém 95% de pregoes
            if ( ((double)historicoCollection.getVolumeFinanceiro() / volumeFinanceiro) < (0.1/100.0)) {
                if (!criteriosExclusao.containsKey(entry.getKey())) {
                    criteriosExclusao.put(entry.getKey(), new ArrayList<CriterioExclusao.CriterioExclusaoEnum>());
                }
                if (!criteriosExclusao.get(entry.getKey()).contains(CriterioExclusao.CriterioExclusaoEnum.ValorFinanceiroAbaixo)) {
                    criteriosExclusao.get(entry.getKey()).add(CriterioExclusao.CriterioExclusaoEnum.ValorFinanceiroAbaixo);
                }
            }
        }
    }

    private void CheckIndiceNegociabilidade(List<IndiceNegociabilidade> indices, Map<String, List<CriterioExclusao.CriterioExclusaoEnum>> criteriosExclusao) {
        //Indices que estiverem acima dos 0.90 serão automaticamente excluídos.
        double limiar = 0.925;
        double limiarNaoAceito = 0.925;

        double cursorLimiar = 0;

        // Ordenando para poder pegar os 85% acumulado do indíce de negóciabilidade
        Collections.sort(indices);

        int x = 0;

        for (IndiceNegociabilidade indiceNeg : indices) {
            boolean pertenceIndice = cursorLimiar < limiarNaoAceito;

            if (!pertenceIndice)
            {
                x++;
                indiceNeg.setCriterioExclusão(String.valueOf(x) + ":" +CriterioExclusao.getDescricao(CriterioExclusao.CriterioExclusaoEnum.Abaixo_90IN));
            }

            indiceNeg.setPercenteIndicice(pertenceIndice);

            if ( pertenceIndice) {
                pertenceIndice = cursorLimiar < limiar;

                if (!pertenceIndice) {
                    if (!criteriosExclusao.containsKey(indiceNeg.getSimbolo())) {
                        criteriosExclusao.put(indiceNeg.getSimbolo(), new ArrayList<CriterioExclusao.CriterioExclusaoEnum>());
                    }
                    if (!criteriosExclusao.get(indiceNeg.getSimbolo()).contains(CriterioExclusao.CriterioExclusaoEnum.Abaixo_85IN)) {
                        criteriosExclusao.get(indiceNeg.getSimbolo()).add(CriterioExclusao.CriterioExclusaoEnum.Abaixo_85IN);
                    }
                }
            }

            cursorLimiar += indiceNeg.getIndiceNegociabilidade();
        }
    }

    private boolean CheckCriterioExclusao(Map<String, HistoricoCollection> historicos, String simbolo, CriterioExclusao.CriterioExclusaoEnum criterioExclusao) {
        boolean existe = false;

        if ( historicos.containsKey(simbolo)) {
            if(historicos.get(simbolo).contains(criterioExclusao))
                return true;
        }

        return existe;
    }
}
