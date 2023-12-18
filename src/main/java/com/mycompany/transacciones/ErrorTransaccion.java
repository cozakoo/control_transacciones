package com.mycompany.transacciones;

public class ErrorTransaccion {

    private int linea;
    private String tipo;

    public ErrorTransaccion(int linea, String tipo) {
        this.linea = linea;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "   Fila N°: " + linea + " de Archivo.\t Error: " + tipo;
    }
}
