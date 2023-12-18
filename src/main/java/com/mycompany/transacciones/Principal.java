package com.mycompany.transacciones;

import interfaz.grafica.Loading;
import interfaz.grafica.SesionUsuario;
import interfaz.grafica.tipoArchivo;
import java.sql.SQLException;

/**
 *
 * @author dgc06
 */
public class Principal {

    public static void main(String[] args) throws Exception {

        try {
            DataBase db = new DataBase();
            db.inicializar();
            if (!db.existeSesion()) {
                db.cleanDB();
                db.cargarEmpresa();
          
                new tipoArchivo(db).setVisible(true);
            
//                db.cleanDB();
            } else {

                SesionUsuario sesion = new SesionUsuario(db);
                sesion.setVisible(true);
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

}
