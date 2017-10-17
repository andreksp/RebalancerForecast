package com.poliusp.monografia.business;
import com.poliusp.monografia.entity.Historico;
import com.poliusp.monografia.entity.HistoricoCollection;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagMaker;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

import java.text.SimpleDateFormat;
import java.util.*;

public class BLPrevisao {


    public void PreverAcao(Date dtInicio, Date dtFim, String simbolo, Map<String, HistoricoCollection> historicos, int diasPrevisao) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dtInicioString = format.format(dtInicio);
        String dtFimString = format.format(dtFim);

        InstanceQuery query = new InstanceQuery();
        query.setDatabaseURL("jdbc:sqlserver://WIN-T0C6086KPD4\\SQLEXPRESS;DatabaseName=Ibovespa;integratedSecurity=true");
        query.setQuery("SELECT DataPregao, QuantidadeNegociada, ValorAcao FROM Historico ht INNER JOIN Acao ac ON ht.idAcao = ac.idAcao" +
                " WHERE Simbolo = '" + simbolo + "' and DataPregao >= '" + dtInicioString + "' and DataPregao <= '" + dtFimString + "' ");
        Instances data = query.retrieveInstances();

        WekaForecaster forecaster = new WekaForecaster();

        //Definindo campos
        forecaster.setFieldsToForecast("QuantidadeNegociada,ValorAcao");

        //Método de previsão
        forecaster.setBaseForecaster(new GaussianProcesses());

        //Parametros da previsão
        forecaster.getTSLagMaker().setTimeStampField("DataPregao");
        forecaster.getTSLagMaker().setPeriodicity(TSLagMaker.Periodicity.DAILY);
        forecaster.getTSLagMaker().setSkipEntries("weekend");
        forecaster.getTSLagMaker().setAddMonthOfYear(true);
        forecaster.buildForecaster(data, System.out);

        forecaster.primeForecaster(data);

        double currentTimestamp = forecaster.getTSLagMaker().getCurrentTimeStampValue();
        java.sql.Date currentDate = new java.sql.Date((long) currentTimestamp);

        List<List<NumericPrediction>> forecast = forecaster.forecast(diasPrevisao, System.out);

        for (int i = 0; i < diasPrevisao; i++) {

            Historico historico = new Historico();

            List<NumericPrediction> predsAtStep = forecast.get(i);

            //Prever quantidade
            NumericPrediction predForTarget = predsAtStep.get(0);
            Double novaQuantidade = predForTarget.predicted();
            historico.setQuantidadeNegociada(novaQuantidade.longValue());

            //Prever preço
            NumericPrediction predictValor = predsAtStep.get(1);
            Double novoPreco = predictValor.predicted();
            historico.setValor(novoPreco);

            //Data da previsão
            double tempTimestamp = forecaster.getTSLagMaker().advanceSuppliedTimeValue(currentTimestamp);
            java.sql.Date dataPrevisao = new java.sql.Date((long) tempTimestamp);
            historico.setDataPregao(dataPrevisao);

            if (!java.lang.Double.isNaN(novoPreco) && novaQuantidade > 0 && novoPreco > 0) {
                //adiciona a previsão como se fizesse parte do histórico
                historicos.get(simbolo).add(historico);
            }
            currentTimestamp = tempTimestamp;

            if (simbolo.equals("ABEV3"))
            {
                System.out.println(simbolo + "\t" +  dataPrevisao.toString() + "\t" + novaQuantidade.toString() + "\t" + novoPreco.toString());
            }
        }
    }
}

