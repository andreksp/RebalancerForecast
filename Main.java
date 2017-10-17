package com.poliusp.monografia;
import java.lang.String;


public class Main {

    public static void main(String[] args) {
        //historical data from http://www.bmfbovespa.com.br/pt_br/servicos/market-data/historico/mercado-a-vista/series-historicas/
        try {

            MainGUI mainGUI = new MainGUI();
            mainGUI.setVisible(true);
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
