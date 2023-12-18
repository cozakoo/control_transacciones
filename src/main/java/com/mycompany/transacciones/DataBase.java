package com.mycompany.transacciones;

import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author dgc06
 */
public class DataBase {

    private Connection conection = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private String sql = null;

    public void inicializar() throws SQLException, IOException, CsvException, Exception {

        Connection con = null;
        conection = DriverManager.getConnection("jdbc:sqlite:./db/data.sqlite");
        stmt = conection.createStatement();
        this.crearTablas();
//        this.cleanDB();
        //    cargarEmpresa();
    }

    public void cleanDB() throws SQLException {
        sql = "delete from `transaccion`;";
        stmt.execute(sql);

        sql = "delete from `fecha_creacion`;";
        stmt.execute(sql);
    }

    private void crearTablas() throws SQLException {
        sql = "PRAGMA foreign_keys = ON;";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS grupo_empresa (\n"
                + "	id_grup_emp INTEGER ,\n"
                + "	grupo TEXT ,\n"
                + "	empresa TEXT,\n"
                + " PRIMARY KEY(id_grup_emp AUTOINCREMENT)"
                + ");";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS transaccion (\n"
                + "	id_grup_emp ,\n"
                + "	tipo_doc TEXT,\n"
                + "	nro_doc TEXT,\n"
                + "	secuencia TEXT,\n"
                + "	cod_concepto TEXT,\n"
                + "	descrip_concepto TEXT,\n"
                + "	cantidad NUMERIC ,\n"
                + "	importe REAL,\n"
                + "	fechaDate TEXT,\n"
                + "	fechaString TEXT,\n"
                + "FOREIGN KEY (id_grup_emp) REFERENCES grupo_empresa (id_grup_emp)"
                + ");";

        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS \"fecha_creacion\"("
                + "\t\"tipo\"\tTEXT,"
                + "\t\"fechaSubida\"\tTEXT,"
                + "\t\"descripcion\"\tTEXT"
                + ");";
        stmt.execute(sql);
    }

    public void cargarEmpresa() throws Exception {
        try {
            sql = "delete from `grupo_empresa`;";
            stmt.execute(sql);
            sql = "delete from `sqlite_sequence`;";
            stmt.execute(sql);
            String dirActual = System.getProperty("user.dir");
            dirActual = dirActual + "\\resource\\grupo-empresa.xlsx";
            Empresa empresa = new Empresa(dirActual);
            importarEmpresa(empresa);
        } catch (IOException | CsvException | SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void importarEmpresa(Empresa empresa) throws Exception {

        empresa.abrir();

        Iterator<org.apache.poi.ss.usermodel.Row> filas = empresa.getHoja().iterator();
        Iterator<org.apache.poi.ss.usermodel.Cell> celdas;
        org.apache.poi.ss.usermodel.Row fila;
        org.apache.poi.ss.usermodel.Cell celda = null;

        if (empresa.isCorrect()) {
            while (filas.hasNext()) {
                fila = filas.next();
                if (fila.getRowNum() >= empresa.getFILA_INICIO()) {
                    celdas = fila.cellIterator();
                    sql = "INSERT INTO grupo_empresa(grupo,empresa) VALUES (?,?)";
                    pstmt = conection.prepareStatement(sql);
                    obtenerValorCeldasEmpresa(celdas, celda);
                    pstmt.executeUpdate();
                }
            }
        } else {
            System.err.println("EL ARCHIVO ES INCORRECTO");
        }
        empresa.cerrar();
    }

    private void obtenerValorCeldasTransaccion(Iterator<Cell> celdas, Cell celda) throws SQLException {
        int columna = 1;
        String empresa, grupo;

        // guardar clave id grupo_empresa
        while (celdas.hasNext()) {
            celda = celdas.next();

            switch (columna) {
                case 1:
                    grupo = celda.getStringCellValue();
                    celda = celdas.next();
                    //obtengo empresa
                    empresa = celda.getStringCellValue();
                    guardarClaveGE(grupo, empresa);
                    columna++;
                    break;
                case 3:
                    guardarLegajo(celda);
                    break;
                case 4:
                    guardarConcepto(celda);
                    break;
                case 5:
                    guardarDescripConcep(celda);
                    break;
                case 6:
                    guardarCantidad(celda);
                    break;
                case 7:
                    guardarImporte(celda);
                    break;
                case 8:
                    guardarFecha(celda);
                    break;
                default:
                    throw new AssertionError();
            }
            columna++;
        }
    }

    private void obtenerValorCeldasEmpresa(Iterator<Cell> celdas, Cell celda) throws SQLException {
        celda = celdas.next();
        pstmt.setString(1, limpiarCelda(celda));
        celda = celdas.next();
        pstmt.setString(2, limpiarCelda(celda));
    }

    //Cadena de primer Caracter en Mayuscula, el resto es minuscula
    private String limpiarCelda(Cell celda) {
        return celda.getStringCellValue().toUpperCase().replace(" ", "");
    }

    public ResultSet consulta(String query) {
        try {
            this.sql = query;
            return stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("filtrar vacio");
        }
        return null;
    }

    public void importarExcel(ExcelTranssacion excel) throws Exception {
        excel.abrir();
        Iterator<Row> filas = excel.getHoja().iterator();
        Iterator<Cell> celdas;
        Row fila;
        Cell celda = null;

        int index = 0;
        if (excel.poseeCabecera()) {
            index = 1;
            filas.next();
        }

        for (int rowIndex = index; rowIndex <= excel.getHoja().getLastRowNum(); rowIndex++) {
            fila = filas.next();
            celdas = fila.cellIterator();
            sql = "INSERT INTO transaccion(id_grup_emp,tipo_doc,nro_doc,secuencia,cod_concepto,descrip_concepto,cantidad,importe,fechaDate, fechaString) \n"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?)";
            pstmt = conection.prepareStatement(sql);
            obtenerValorCeldasTransaccion(celdas, celda);
            pstmt.executeUpdate();
        }

        cargarFechaExcel(excel);
        System.err.println("ARCHIVO CARGADO CORRECTAMENTE");
    }

    public void importarCsv(Csv csv) throws SQLException {

        //  int i = 0;
        for (String[] arrays : csv.getR()) {
            //         System.out.println("Procesando fila: " + i);
            sql = "INSERT INTO transaccion(id_grup_emp,tipo_doc,nro_doc,secuencia,cod_concepto,descrip_concepto,cantidad,importe,fechaDate,fechaString)VALUES(?,?,?,?,?,?,?,?,?,?)";
            pstmt = conection.prepareStatement(sql);
            obtenerValorCvc(csv, arrays);

            pstmt.executeUpdate();
            //    i++;
        }
        cargarFechaCsv(csv);
    }

    private void obtenerValorCvc(Csv csv, String[] arrays) throws SQLException {

        //index es mi columna del csv
        int index = 0;
        String grupo_empresa[] = new String[2];
        int columnaDB = 0;
        String valor;

        for (String array : arrays) {
            switch (csv.obtenerTipoDatoColumna(index)) {

                case 0:
                    valor = array.replace(" ", "");

                    //LA COLUMA ES DE MI GRUPO_EMPRESA
                    if (index == 0) {
                        grupo_empresa[1] = valor;
                    } else {
                        grupo_empresa[0] = valor;

                        sql = "SELECT * from grupo_empresa e where e.grupo = " + "'" + grupo_empresa[1] + "' AND e.empresa = '" + grupo_empresa[0] + "';";
                        ResultSet res = consulta(sql);
                        pstmt.setString(columnaDB, res.getString(1));
                    }
                    columnaDB++;
                    break;

                case 1:
                    valor = array.replace(" ", "");

                    pstmt.setString(columnaDB, csv.obtenerTipoDocumento(valor));
                    columnaDB++;
                    pstmt.setString(columnaDB, csv.obtenerNroDocumento(valor));
                    columnaDB++;
                    pstmt.setString(columnaDB, csv.obteneNroSecuencia(valor));
                    columnaDB++;
                    break;
                case 2:
                    if (index != 4) {
                        valor = array.replace(" ", "");
                    } else {
                        valor = array;
                    }
                    pstmt.setString(columnaDB, valor);
                    columnaDB++;
                    break;

                case 3: //SETEO COMO ENTERO
                    valor = array.replace(" ", "");

                    String cadenaSinEspacios = valor;
                    String cadenaSinEspacios_y_SinComas = cadenaSinEspacios.replace(",", "");
                    cadenaSinEspacios_y_SinComas = cadenaSinEspacios_y_SinComas.replace(".", "");

                    Double cadenaComoDouble = Double.parseDouble(cadenaSinEspacios_y_SinComas);

                    if (cadenaSinEspacios.equals(cadenaSinEspacios_y_SinComas)) {
                        pstmt.setDouble(columnaDB, cadenaComoDouble);
                    } else {
                        int cadenaComoEntero = Integer.parseInt(cadenaSinEspacios_y_SinComas);
                        pstmt.setInt(columnaDB, cadenaComoEntero / 100);
                    }
                    columnaDB++;
                    break;
                case 4:
                    valor = array.replace(" ", "");
                    pstmt.setString(columnaDB, csv.transformarFecha(array));
                    columnaDB++;
                    pstmt.setString(columnaDB, valor);
            }
            index++;
        }
    }

    private boolean vericarId(String array) {
        return true;
    }

    private String limpiar(Cell celda) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void guardarLegajo(Cell cell) throws SQLException {
        DecimalFormat decimalFormat = new DecimalFormat("0");
        String legajo = decimalFormat.format(cell.getNumericCellValue());
        String tipodoc = legajo.substring(0, 3);
        String doc = legajo.substring(3, 11);
        String seq = legajo.substring(11, 13);
        pstmt.setString(2, tipodoc);
        pstmt.setString(3, doc);
        pstmt.setString(4, seq);
    }

    private void guardarClaveGE(String grupo, String empresa) throws SQLException {

        sql = "SELECT id_grup_emp from grupo_empresa where grupo='" + grupo + "' AND empresa='" + empresa + "'";
        ResultSet res = this.consulta(sql);
        int clave = Integer.parseInt(res.getString(1));
        pstmt.setInt(1, clave);
    }

    private void guardarConcepto(Cell cell) throws SQLException {
        int cod_concep = (int) cell.getNumericCellValue();
        pstmt.setString(5, Integer.toString(cod_concep));
    }

    private void guardarCantidad(Cell cell) throws SQLException {
        int cantidad = (int) cell.getNumericCellValue();
        pstmt.setInt(7, cantidad);
    }

    private void guardarDescripConcep(Cell cell) throws SQLException {
        pstmt.setString(6, cell.getStringCellValue());
    }

    private void guardarImporte(Cell cell) throws SQLException {
        try {
            double importe = (double) cell.getNumericCellValue();
            DecimalFormat formato = new DecimalFormat("#.##");
            String numeroFormateado = formato.format(importe);
            importe = formato.parse(numeroFormateado).doubleValue();
            pstmt.setDouble(8, importe);
        } catch (ParseException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void guardarFecha(Cell cell) throws SQLException {

        Date date = cell.getDateCellValue();
        LocalDate fecha = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String fechaString = fecha.format(formatter);
        pstmt.setString(9, fechaString);
        pstmt.setString(10, fechaString.replaceAll("/", ""));
    }

    public void exportarCSV(File archivo, String sqlFilter, String nombreArchivo) throws SQLException {
        try {
            ResultSet rs = consulta(sqlFilter);
            System.out.println("CONSULTAS DEL REPORTE");

            // Obtener la ruta completa del archivo, incluyendo la ubicación
            String nombrePredeterminado = "T_";
            int maxLength = 15 - nombrePredeterminado.length();
            String rutaCompleta;

            if (nombreArchivo.length() > maxLength) {
                rutaCompleta = archivo.getParent() + "\\" + nombrePredeterminado + archivo.getName().substring(0, maxLength) + ".csv";
            } else {
                rutaCompleta = archivo.getParent() + "\\" + nombrePredeterminado + archivo.getName();
            }
            Reporte csv_exportado = new Reporte(rutaCompleta, rs);
            csv_exportado.exportar();
        } catch (IOException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean existeSesion() throws SQLException {
        return CantFilasCargadas("transaccion") > 0;
    }

    public int CantFilasCargadas(String tabla) throws SQLException {
        //return cantidad filas de una tabla
        sql = "SELECT COUNT(*) AS total FROM " + tabla + ";";
        ResultSet result = stmt.executeQuery(sql);
        Object o = result.getObject(1);
        return (int) o;
    }

    private void cargarFechaExcel(ExcelTranssacion excel) throws SQLException {
        sql = "INSERT INTO fecha_creacion(tipo, fechaSubida, descripcion) VALUES (?,?,?)";
        pstmt = conection.prepareStatement(sql);
        pstmt.setString(1, "EXCEL");
        pstmt.setString(2, excel.getFechaComoCadena());
        pstmt.setString(3, excel.getRuta());
        pstmt.executeUpdate();
    }

    private void cargarFechaCsv(Csv csv) throws SQLException {
        sql = "INSERT INTO fecha_creacion(tipo, fechaSubida, descripcion) VALUES (?,?,?)";

        pstmt = conection.prepareStatement(sql);

        pstmt.setString(1, "CSV");
        pstmt.setString(2, csv.getFechaComoCadena());
//        pstmt.setString(3, liresto.getPrimerFechaLiquidacion());
        pstmt.setString(3, csv.getRuta());
        pstmt.executeUpdate();
    }
}
