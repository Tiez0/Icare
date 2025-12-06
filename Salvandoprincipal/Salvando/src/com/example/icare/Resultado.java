package com.example.icare;
public class Resultado extends Comunicado {
    private boolean valido;

    public Resultado(boolean valido) {
        this.valido = valido;
    }

    public boolean isValido() {
        return this.valido;
    }

    // Opcional: para facilitar impressão se necessário
    public String toString() {
        return this.valido ? "Válido" : "Inválido";
    }
}