package com.mycompany.transacciones;

public interface Iverification {

    //constantes que definen los tipos de las columas
    /**
     * columna 0: id_empresa columna 1: grupo columna (2,3,4): legajo columna 5:
     * cod_concepto columna 6: descrip_concepto columna 7: cantidad columna 8:
     * importe columna 9: fecha
     *
     */

    //COLUMNAS AGRUPADAS POR TIPO DE DATO
    final int[] COLUMNAS_GRUPO_EMPRESA = {0, 1};
    final int COLUMNA_LEGAJO = 2; // utilizado para dividir la columna
    final int[] COLUMNAS_STRING = {3, 4, 6};
    final int COLUMNA_CANTIDAD = 5;

    //CONTANTES PARA VERIFICAR LONGITURES
    final int LONGITUD_LEGAJO = 13;
    final int LONGITUD_CODIGO_CONCEPTO = 4;
    final int LONGITUD_DESCRIPCION_CONCEPTO = 30;
    final int LONGITUD_CANTIDAD = 3;
    final int LONGITUD_IMPORTE = 17;
    final int LONGITUD_FECHA = 8;

    //CONSTANTES PARA VERIFICAR MAXIMOS Y MINIMOS
    //supuestamente el numero documento no puede ser mayor a 4.000.000
    final int MAXIMO_DOCUMENTO = 99999999;
    //CONSTANTES QUE AYUDARAN A VALIDAR LA FECHA
    final int MAX_MES = 12;
    final int MAX_DIA = 31;
    final int TOTAL_COLUMNAS_ARCHIVO = 8;
    final int[] TIPOS_DOCUMENTO = {100, 200, 300, 500};

    /**
     * Verifia que mi archivo sea correcto.
     * @return 
     */
    public abstract boolean isValid();

    /**
     * Listar Errores. Devuelve una lista con todos los errores de mi CSV ||
     * Excel
     */
    public abstract void listarErrores();
}
