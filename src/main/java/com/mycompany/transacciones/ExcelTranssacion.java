package com.mycompany.transacciones;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelTranssacion implements Iverification {

    private DataBase db = null;
    private String ruta = null;
    private FileInputStream file;
    private XSSFWorkbook libro;
    private XSSFSheet hoja;

    // atributos de la transaccion
    private String legajo, descrip;
    private int cantidad = 0;
    private double importe;
    private int cod_concep;
    private List<ErrorTransaccion> listErrores;
    private final SimpleDateFormat dateFormat;
    private final String fechaComoCadena;
    private boolean daniado = false;

    public ExcelTranssacion(String ruta, DataBase db) {
        this.ruta = ruta;
        this.db = db;
        this.dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm aaa");
        this.fechaComoCadena = dateFormat.format(new Date());
    }

    public void abrir() throws FileNotFoundException, IOException, Exception {
        //  verificarDañado();
        limpiar();
        this.file = new FileInputStream(this.ruta);
        this.libro = new XSSFWorkbook(file);
        //seleccionamos la primera hoja
        this.hoja = libro.getSheetAt(0);
    }

    private void limpiar() throws Exception {

        String ruta = this.ruta;

        Workbook workbook = new Workbook(this.ruta);
        // Obtenga la colección de hojas de trabajo en la hoja de cálculo

        WorksheetCollection sheets = workbook.getWorksheets();
        //Obtenga la primera hoja de trabajo de WorksheetCollection por índice

        Worksheet sheet = sheets.get(0);
        //Elimina las columnas en blanco.

        sheet.getCells().deleteBlankColumns();
        //Guarde el archivo de Excel actualizado

        workbook.save(this.ruta);

    }

    public void cerrar() throws IOException {
        this.file.close();
        this.libro.close();
    }

    @Override
    public void listarErrores() {
        this.listErrores = new ArrayList<>();
        int index = 0;
        if (poseeCabecera()) {
            index = 1;
        }

        for (int rowIndex = index; rowIndex <= hoja.getLastRowNum(); rowIndex++) {

            Row row = hoja.getRow(rowIndex);
            try {
                controlarFila(row, rowIndex);

            } catch (SQLException ex) {
                Logger.getLogger(ExcelTranssacion.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<ErrorTransaccion> getErrores() {
        return this.listErrores;
    }

    private void controlarFila(Row row, int indexRow) throws SQLException {
        int index = 1;
        String grupo_empresa[] = new String[2];

        for (Cell cell : row) {
            switch (index) {
                case 1:
                    grupo_empresa[1] = cell.toString();
                    break;

                case 2:
                    grupo_empresa[0] = cell.toString();
                    Verifica.verificarGrupoEmpresa(grupo_empresa, indexRow + 1, listErrores, db);
                    break;

                case 3:
                    DecimalFormat decimalFormat = new DecimalFormat("0");
                    try {
                        legajo = decimalFormat.format(cell.getNumericCellValue());
                        Verifica.verificarLegajo(legajo, indexRow, listErrores);
                    } catch (IllegalStateException e) {
                        Mensaje msj = new Mensaje();
                        listErrores.add(new ErrorTransaccion(indexRow + 1, msj.legajoFormatoIncorrecto()));
//                    } catch (NumberFormatException e) {
//                        Mensaje msj = new Mensaje();
//                        listErrores.add(new ErrorTransaccion(indexRow + 1, msj.legajoFormatoIncorrecto()));
                    }
                    break;

                case 4:
                try {
                    cod_concep = (int) cell.getNumericCellValue();
                    Verifica.verificarCodigoConcepto(Integer.toString(cod_concep), indexRow + 1, listErrores);
                } catch (NumberFormatException e) {
                    Mensaje msj = new Mensaje();
                    listErrores.add(new ErrorTransaccion(indexRow + 1, msj.codigoConceptoFormatoInvalido()));
                }
                break;

                case 5:
                    descrip = cell.getStringCellValue();
                    Verifica.verificarDescripcionConcepto(descrip, indexRow + 1, listErrores);
                    break;

                case 6:
                    cantidad = (int) cell.getNumericCellValue();
                    String cantidadString = Integer.toString(cantidad);
                    Verifica.verificarCantidad(cantidadString, indexRow + 1, listErrores);
                    break;

                case 7:
                    importe = cell.getNumericCellValue();
                    String importeString = String.format("%.2f", importe);

                    Verifica.verificarImporte(importeString, indexRow + 1, listErrores);
                    break;

                case 8:
                    Date date = cell.getDateCellValue();
                    LocalDate fecha = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    String fechaArchivo = fecha.toString().replaceAll("-", "");
                    Verifica.verificarFecha(fechaArchivo, indexRow + 1, listErrores);
//                    LocalDate fecha = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                    System.out.println(fecha);
//                    verificarFormatoFecha(fecha, indexRow, listErrores);
                    break;
            }
            index++;
        }
    }

    @Override
    public boolean isValid() {
        Row row = hoja.getRow(1);
        return !((row.getLastCellNum() > TOTAL_COLUMNAS_ARCHIVO) || (row.getLastCellNum() < TOTAL_COLUMNAS_ARCHIVO));
    }

    public XSSFSheet getHoja() {
        return hoja;
    }

    public String getRuta() {
        return ruta;
    }

    public String getFechaComoCadena() {
        return fechaComoCadena;
    }

    public boolean poseeCabecera() {

        boolean poseeCabecer = true;
        Row row = hoja.getRow(0);

        // Verificar si al menos una celda en la primera fila tiene un valor
        for (Cell cell : row) {
            if (cell.getCellTypeEnum() == CellType.NUMERIC) {

                return poseeCabecer = false; // Hay una cabecera

            }

        }

        return poseeCabecer;

    }

}
