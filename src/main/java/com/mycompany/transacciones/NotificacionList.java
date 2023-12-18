package com.mycompany.transacciones;

import interfaz.grafica.Notificacion;
import java.util.ArrayList;

/**
 *
 * @author dgc06
 */
public class NotificacionList {

    private static NotificacionList instance;
    private static ArrayList<String>  listNotificacion;
    public static NotificacionList getInstance() {
        if (instance == null) {
            instance = new NotificacionList();
            listNotificacion = new ArrayList<>();
        }
        return instance;
    }
   public void agregar(String text){
       listNotificacion.add(text);
   }
   
   public void MostrarNotificaciones(){
       for (String notificacion : listNotificacion) {
            new Notificacion(notificacion).setVisible(true);
        }
   }
}
