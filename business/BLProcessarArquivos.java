package com.poliusp.monografia.business;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BLProcessarArquivos {
    BLHistorico blHistorico;
    public BLProcessarArquivos() throws SQLException, ClassNotFoundException {
        blHistorico = new BLHistorico();
    }

    public void lerArquivo(String file) throws FileNotFoundException, IOException, ParseException, SQLException, ClassNotFoundException {
        BufferedReader bufferReader = new BufferedReader(new FileReader(file));

        //pulando primeira linha.
        String linha = bufferReader.readLine();

        while(linha != null)
        {
            linha = bufferReader.readLine();

            if (linha != null)
            {
                String tipoLinha = linha.substring(0, 2).trim();

                if ( tipoLinha.equals("01")) {
                    DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    Date data = (Date) formatter.parse(linha.substring(2, 10));

                    String simbolo = linha.substring(12, 24).trim();
                    double valor = Long.parseLong(linha.substring(110, 121)) / 100.0;
                    String bdi = linha.substring(10, 12).trim();
                    String especificacaoPapel = linha.substring(39, 48).trim();
                    long quantidadeNegociada = Long.parseLong(linha.substring(154, 170));

                    if ( bdi.equals("02") && !especificacaoPapel.equals("IBO")) {
                        blHistorico.AdicionHistorico(simbolo, data, valor, quantidadeNegociada);
                    }
                }
            }
        }
    }
}
