package com.mycompany.transacciones;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Empresa {

    private final int FILA_INICIO = 2;

    // Atributos
    private String ruta = null;
    private FileInputStream file;
    private XSSFWorkbook libro;
    private XSSFSheet hoja;

    public Empresa(String ruta) throws IOException, Exception {
        this.ruta = ruta;
    }

    public void abrir() throws FileNotFoundException, IOException, Exception {
        limpiar();
        this.file = new FileInputStream(this.ruta);
        this.libro = new XSSFWorkbook(file);
        //seleccionamos la primera hoja
        this.hoja = libro.getSheetAt(0);
    }

    private void limpiar() throws Exception {
        Workbook workbook = new Workbook(this.ruta);
        // Obtenga la colección de hojas de trabajo en la hoja de cálculo.
        WorksheetCollection sheets = workbook.getWorksheets();
        // Obtenga la primera hoja de trabajo de WorksheetCollection por índice.
        Worksheet sheet = sheets.get(0);
        // Elimina las columnas en blanco.
        sheet.getCells().deleteBlankColumns();
        // Guarde el archivo de Excel actualizado.
        workbook.save(this.ruta);
    }

    public void cerrar() throws IOException {
        this.file.close();
        this.libro.close();
    }

    public boolean isCorrect() {
        return true;
    }

    public XSSFSheet getHoja() {
        return hoja;
    }

    public int getFILA_INICIO() {
        return FILA_INICIO;
    }
}
