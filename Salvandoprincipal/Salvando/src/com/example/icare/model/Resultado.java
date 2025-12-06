package com.example.icare.model;

public class Resultado extends Comunicado {
    private boolean valido;

    public Resultado(boolean valido) {
        this.valido = valido;
    }

    public boolean isValido() {
        return this.valido;
    }
}