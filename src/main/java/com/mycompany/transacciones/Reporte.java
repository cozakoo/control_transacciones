package com.mycompany.transacciones;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Reporte {

    private String ruta;
    private ResultSet rs;
    private List datos;

    public Reporte(String ruta, ResultSet rs) throws SQLException, IOException {
        this.ruta = ruta;
        this.rs = rs;
        this.datos = new ArrayList<String>();
        generarObjetos();
    }

    private void generarObjetos() throws SQLException {

        while (rs.next()) {
            datos.add(
                    "\"" + rs.getString(1) + "\"" + ";"
                    + "\"" + rs.getString(2) + "\"" + ";"
                    + "\"" + rs.getString(3)
                    + rs.getString(4)
                    + rs.getString(5) + "\"" + ";"
                    + "\"" + rs.getString(6) + "\"" + ";"
                    + "\"" + rs.getString(7) + "\"" + ";"
                    + "\"" + rs.getString(8) + "\"" + ";"
                    + "\"" + rs.getString(9).replace(".", ",") + "\"" + ";"
                    + "\"" + rs.getString(11) + "\"" //                rs.getString(11)

            );
        }
    }

    public void exportar() throws SQLException, IOException {
        try (FileWriter writer = new FileWriter(ruta)) {

            for (Object linea : datos) {
                writer.append(linea.toString());
                writer.append("\r\n");
            }
        }
    }
}
