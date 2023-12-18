package com.mycompany.transacciones;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Csv implements Iverification {

    //Atributos
    private CSVParser csvParser;
    private String ruta;
    private List<String[]> r;
    private DataBase db;
    private List<ErrorTransaccion> listErrores;
    private final SimpleDateFormat dateFormat;
    private final String fechaComoCadena;

    public Csv(String ruta, DataBase db) throws IOException, CsvException {
        this.ruta = ruta;
        this.csvParser = new CSVParserBuilder().withSeparator(';').build();
        this.db = db;
        leerArchivo();
        this.dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm aaa");
        this.fechaComoCadena = dateFormat.format(new Date());
    }

    public List<String[]> getR() {
        return r;
    }

// Método para leer el archivo, omitiendo la cabecera si existe
    private void leerArchivo() throws IOException, CsvException {
        boolean tieneCabecera = tieneCabecera();
        int lineasAExcluir = tieneCabecera ? 1 : 0; // Si hay cabecera, excluye una línea, de lo contrario, no excluye ninguna

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(this.ruta))
                .withCSVParser(this.csvParser) // Custom CSV parser
                .withSkipLines(lineasAExcluir) // Salta las líneas iniciales, que pueden ser la cabecera
                .build()) {
            this.r = reader.readAll();
        }
    }

    @Override
    public boolean isValid() {
        String[] linea = r.get(0);
        return linea.length == TOTAL_COLUMNAS_ARCHIVO;
    }

    public int obtenerTipoDatoColumna(int index) {
        //Buscamos si mi columa es de las columas GRUPO-EMPRESA
        boolean encontro = Arrays.stream(COLUMNAS_GRUPO_EMPRESA).boxed().collect(Collectors.toSet()).contains(index);
        if (encontro) {
            return 0;
        } //mi columna es de grupo_empresa
        else {
            if (index == COLUMNA_LEGAJO) {
                return 1;
            } else {
                encontro = Arrays.stream(COLUMNAS_STRING).boxed().collect(Collectors.toSet()).contains(index);
                if (encontro) {
                    return 2;
                } else {
                    if (index == COLUMNA_CANTIDAD) {
                        return 3;
                    } else {
                        //mi columna es de fecha
                        return 4;
                    }
                }
            }
        }
    }

    public String obtenerTipoDocumento(String array) {
        return array.substring(0, 3);  //050 
    }

    public String obtenerNroDocumento(String array) {
        return array.substring(3, 11);  //11193009
    }

    public String obteneNroSecuencia(String array) {
        return array.substring(11, 13);  //00
    }

    public String transformarFecha(String array) {

        String anio = array.substring(0, 4);  //050 
        String mes = array.substring(4, 6);  //050 
        String dia = array.substring(6, 8);  //050 

        return anio + "/" + mes + "/" + dia;
    }

    @Override
    public void listarErrores() {

        this.listErrores = new ArrayList<>();
        int numFila = 1;
        for (String[] arrays : getR()) {
            try {
                obtenerErrores(arrays, listErrores, numFila);
                numFila++;
            } catch (SQLException ex) {
                Logger.getLogger(Csv.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void obtenerErrores(String[] arrays, List<ErrorTransaccion> list, int numRow) throws SQLException {

        String grupo_empresa[] = new String[2];
        //index es mi columna del csv
        int index = 0;

        for (String array : arrays) {
            String valor = array.replace(" ", "");

            switch (obtenerTipoDatoColumna(index)) {

                case 0: //columnas de GrupoEmpresa
                    //LA COLUMA ES DE MI GRUPO_EMPRESA
                    if (index == 0) {
                        grupo_empresa[1] = valor;
                    } else {
                        grupo_empresa[0] = valor;
                        Verifica.verificarGrupoEmpresa(grupo_empresa, numRow, listErrores, db);
                    }
                    break;

                case 1: //columa especifica del legajo
                    Verifica.verificarLegajo(valor, numRow, listErrores);
                    break;

                case 2: //es un String
                    if (index == 3) { //es la columna de Codigo Concepto
                        Verifica.verificarCodigoConcepto(valor, numRow, listErrores);
                    } else {
                        if (index == 4) {
                            Verifica.verificarDescripcionConcepto(valor, numRow, listErrores);
                        } else {
                            Verifica.verificarImporte(valor, numRow, listErrores);
                        }
                    }
                    break;

                case 3:
                    Verifica.verificarCantidad(valor, numRow, listErrores);
                    break;

                case 4:
                    Verifica.verificarFecha(valor, numRow, listErrores);
                    break;
            }
            index++;
        }
    }

    public List<ErrorTransaccion> getErrores() {
        return this.listErrores;
    }

    public String getFechaComoCadena() {
        return fechaComoCadena;
    }

    public String getRuta() {
        return ruta;
    }

    private boolean tieneCabecera() throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(this.ruta))
                .withCSVParser(this.csvParser) // Custom CSV parser
                .build()) {
            // Lee la primera línea del archivo CSV
            String[] primeraLinea = reader.readNext();

            // Verifica si la primera línea parece ser una fila de cabecera
            if (primeraLinea != null) {
                if ((primeraLinea[0].length() != 2) || (primeraLinea[1].length() != 2)){
                    return true;
                }
            }
            return false;
        }
    }

}
