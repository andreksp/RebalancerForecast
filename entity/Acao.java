package com.poliusp.monografia.entity;

public class Acao {

    public int GetIdAcao()
    {
        return idAcao;
    }

    public void SetIdAcao(int idAcao)
    {
        this.idAcao = idAcao;
    }
    private int idAcao;

    public String GetSimbolo()
    {
        return simbolo;
    }

    public void SetSimbolo(String simbolo)
    {
        this.simbolo = simbolo.trim();
    }

    private String simbolo;
}
