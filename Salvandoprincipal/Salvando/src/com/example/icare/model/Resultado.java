package com.example.icare.model;

public class Resultado extends Comunicado {
    private boolean valido;
    private String mensagem;


    public Resultado(boolean valido, String mensagem) {
        this.valido = valido;
        this.mensagem = mensagem;
    }

    public boolean isValido() {
        return this.valido;
    }

    public String getMensagem() {
        return this.mensagem;
    }
}