package com.mycompany.transacciones;

import static com.mycompany.transacciones.Iverification.LONGITUD_CANTIDAD;
import static com.mycompany.transacciones.Iverification.LONGITUD_DESCRIPCION_CONCEPTO;
import static com.mycompany.transacciones.Iverification.LONGITUD_FECHA;
import static com.mycompany.transacciones.Iverification.LONGITUD_IMPORTE;
import static com.mycompany.transacciones.Iverification.LONGITUD_LEGAJO;
import static com.mycompany.transacciones.Iverification.MAXIMO_DOCUMENTO;
import static com.mycompany.transacciones.Iverification.TOTAL_COLUMNAS_ARCHIVO;

/**
 *
 * Clase destinada a mostrar errores por pantalla en caso de cualquier
 * invalidacion
 */
public class Mensaje {

    //MENSAJES DE ERRORES RELACIONADOS CON LOS ARCHIVOS
    
     public String excedeTamaño() {
        return "EL archivo es muy pesado, no puede ser procesado";
    }
    public String excedeColumas() {
        return "La fila excede las " + TOTAL_COLUMNAS_ARCHIVO + " columnas";
    }

    // MENSAJES DE ERRORES RELACIONADOS AL GRUPO-EMPRESA
    public String grupoEmpresaNoExiste() {
        return "La combinacion de Grupo-Empresa no existe";
    }

    // MENSAJES DE ERRORES RELACIONADOS AL LEGADO (Tipo, Documento, secuencia
    public String legajoFormatoIncorrecto() {
        return "legajo con formato incorrecto, no es numerico";
    }

    public String longitud_Legajo_Mayor() {
        return "La longitud del legajo es superior a los " + LONGITUD_LEGAJO + " caracteres";
    }

    public String longitud_Legajo_Menor() {
        return "La longitud del legajo es inferior a los " + LONGITUD_LEGAJO + " caracteres";
    }

    public String tipoDocumentoNoExiste() {
        return "El tipo documento no existe";
    }

    public String numeroDocumento_Mayor() {
        return "El numero de documento excede el limite, no puede ser mayor a " + MAXIMO_DOCUMENTO;
    }

    // MENSAJES DE ERRORES RELACIONADOS AL CODIGO CONCEPTO
    public String codigoConceptoLongitudMayor() {
        return "El codigo concepto es incorrecto";
    }

    public String codigoConceptoLongitudMenor() {
        return "El codigo concepto es incorrecto";
    }

    public String codigoConceptoFormatoInvalido() {
        return "el codigo de concepto debe ser un numero";
    }

    // MENSAJES DE ERRORES RELACIONADOS A LA DESCRIPCION DEL CONCEPTO
    public String descripcionConceptoLongitudMayor() {
        return "la descripcion del concepto supera el maximo de " + LONGITUD_DESCRIPCION_CONCEPTO + " caracteres";
    }

    public String descripcionConceptoLongitudMenor() {
        return "la descripcion del concepto es menor a la longitud de " + LONGITUD_DESCRIPCION_CONCEPTO + " caracteres";
    }

    public String cantidadLongitudMayor() {
        return "La cantidad supera el maximo de " + LONGITUD_CANTIDAD + " digitos";
    }

    public String importeLongitudMayor() {
        return "El importe supera el maximo de " + LONGITUD_IMPORTE + " digitos";
    }

    public String signosImporteCantidadDistintos() {
        return "El importe y la cantidad deben tener el mismo signo";
    }

    public String fechaLongitudMayor() {
        return "La fecha supera los " + LONGITUD_FECHA + " digitos";
    }

    public String fechaLongitudMenor() {
        return "La fecha no tiene los " + LONGITUD_FECHA + " digitos";
    }

    public String noEsMes() {
        return "El mes es incorrecto";
    }

    public String noEsDia() {
        return "El dia es incorrecto";
    }

    public String formatoInvalido_Numerico(String legajo) {
        return "Formato invalido. En el " + legajo + " tiene que haber solo numeros";
    }

    public String descipcionVacia() {
        return "La descripcion no puede ser vacia";
    }

    public String añoAnterior(int index) {
        return "Fila N°:" + index + " la fecha esta en el año anterior";
    }
    
    public String fechaMayorActual(int index) {
        return "Fila N°:" + index + " la fecha es posterior a la actual";
    }
}
