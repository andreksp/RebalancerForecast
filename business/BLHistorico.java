package com.poliusp.monografia.business;

import com.poliusp.monografia.dataaccess.DaHelper;
import org.jcp.xml.dsig.internal.dom.ApacheCanonicalizer;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.poliusp.monografia.entity.*;
import java.text.*;

public class BLHistorico {

    public BLAcao  blAcao = null;
    public DaHelper daHelper;

    public BLHistorico() throws SQLException, ClassNotFoundException {
        blAcao = new BLAcao();
        daHelper = new DaHelper();
    }

    public void AdicionHistorico(String simbolo, Date dataPregao, double valor, long quantidadeNegociada) throws SQLException, ClassNotFoundException {

        Acao acao = blAcao.GetAcao(simbolo);

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        String dataFormatada = dt.format(dataPregao);

        ResultSet simboloRes = daHelper.ExecututeReader("SELECT 1 FROM HISTORICO WHERE IdAcao = '" + acao.GetIdAcao() + "' and DataPregao = '" + dataFormatada + "'");

        if (!simboloRes.next()) {
            daHelper.ExecututeQuery("INSERT INTO [dbo].[Historico]" +
                    "           ([IdAcao]" +
                    "           ,[DataPregao]" +
                    "           ,[ValorAcao]" +
                    "           ,[QuantidadeNegociada])" +
                    "     VALUES" +
                    "           (" + acao.GetIdAcao() + "" +
                    "           ,'" + dataFormatada + "'" +
                    "           ," + valor + "" +
                    "           ," + quantidadeNegociada + ")");
        }
    }

    public Map<String,HistoricoCollection> ObterHistorico(Date dtInicio, Date dtFim) throws SQLException, ClassNotFoundException, ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dtInicioString = format.format(dtInicio);
        String dtFimString = format.format(dtFim);

        Map<String, HistoricoCollection> mapHist = new HashMap<String, HistoricoCollection>();

        ResultSet historicos = daHelper.ExecututeReader("SELECT ht.*, ac.Simbolo FROM Historico ht INNER JOIN Acao ac ON ht.idAcao = ac.idAcao" +
                " WHERE DataPregao >= '" + dtInicioString + "' and DataPregao <= '" + dtFimString + "' ");

        while (historicos.next()) {
            String simbolo = historicos.getString("Simbolo");

            if (!mapHist.containsKey(simbolo)) {
                mapHist.put(simbolo, new HistoricoCollection());
            }

            mapHist.get(simbolo).add(CriarHistorico(historicos));
        }
        return mapHist;
    }

    private Historico CriarHistorico(ResultSet historicos) throws SQLException, ParseException {
        Historico historico = new Historico();
        historico.setSimbolo(historicos.getString("Simbolo"));

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date data = (Date)formatter.parse(historicos.getString("DataPregao"));
        historico.setDataPregao(data);

        historico.setValor(Double.parseDouble(historicos.getString("ValorAcao")));

        historico.setQuantidadeNegociada(Long.parseLong(historicos.getString("QuantidadeNegociada")));

        historico.setIdAcao(Integer.parseInt(historicos.getString("idAcao")));

        return historico;
    }
}
