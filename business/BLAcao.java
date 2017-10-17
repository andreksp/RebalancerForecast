package com.poliusp.monografia.business;
import java.sql.*;
import java.util.*;

import com.poliusp.monografia.dataaccess.DaHelper;
import com.poliusp.monografia.entity.*;

public class BLAcao {

    public DaHelper daHelper;

    public BLAcao() throws SQLException, ClassNotFoundException {
        daHelper = new DaHelper();
        LerAcoes();
    }

    Map<String, Acao> todasAcoes = new HashMap<String, Acao>();

    public Acao GetAcao(String simbolo) throws SQLException, ClassNotFoundException {
        if (todasAcoes.containsKey(simbolo)) {
            return todasAcoes.get(simbolo);
        }
        else
        {
            return AdicionarProduto(simbolo);
        }
    }

    public void LerAcoes() throws SQLException, ClassNotFoundException {
        ResultSet acoes = daHelper.ExecututeReader("SELECT * FROM Acao");

        while(acoes.next())
        {
            CriarAcao(acoes);
        }
    }

    private Acao CriarAcao(ResultSet acoes) throws SQLException {
        Acao acao = new Acao();
        acao.SetIdAcao(Integer.parseInt(acoes.getString("IdAcao")));
        acao.SetSimbolo(acoes.getString("Simbolo"));

        if (!todasAcoes.containsKey(acao.GetSimbolo())) {
            todasAcoes.put(acao.GetSimbolo(), acao);
        }

        return acao;
    }

    public Acao AdicionarProduto(String simbol) throws SQLException, ClassNotFoundException {
        ResultSet simboloRes = daHelper.ExecututeReader("SELECT 1 FROM Acao WHERE Simbolo = '" + simbol + "'");

        if(!simboloRes.next())
        {
            daHelper.ExecututeQuery("INSERT INTO ACAO (SIMBOLO) VALUES('" + simbol.trim() +"')");

            ResultSet simbolo = daHelper.ExecututeReader("SELECT * FROM Acao WHERE Simbolo = '" + simbol.trim() + "'");

            if(simbolo.next()) {
                return CriarAcao(simbolo);
            }
        }

        return null;
    }
}
