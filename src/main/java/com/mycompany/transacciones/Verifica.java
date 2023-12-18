package com.mycompany.transacciones;

import static com.mycompany.transacciones.Iverification.LONGITUD_CANTIDAD;
import static com.mycompany.transacciones.Iverification.LONGITUD_CODIGO_CONCEPTO;
import static com.mycompany.transacciones.Iverification.LONGITUD_DESCRIPCION_CONCEPTO;
import static com.mycompany.transacciones.Iverification.LONGITUD_FECHA;
import static com.mycompany.transacciones.Iverification.LONGITUD_IMPORTE;
import static com.mycompany.transacciones.Iverification.LONGITUD_LEGAJO;
import static com.mycompany.transacciones.Iverification.MAXIMO_DOCUMENTO;
import static com.mycompany.transacciones.Iverification.MAX_MES;
import static com.mycompany.transacciones.Iverification.TIPOS_DOCUMENTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Clase destinada a Verificar los argumentos
 *
 * @author DGC
 */
public class Verifica {

    private static Mensaje msj = new Mensaje();
    private static boolean esCantidadNegativa;
    private static boolean esImporteNegativo;
    private static boolean esCantidadCero;

    /**
     * Consulta a la base de datos si existe el grupo y la empresa. Si no existe
     * lo agrega en la lista de errores para mostrar por pantalla
     *
     * @param grupo_empresa
     * @param index
     * @param listErrores
     * @param db
     * @throws SQLException
     */
    public static void verificarGrupoEmpresa(String[] grupo_empresa, int index, List<ErrorTransaccion> listErrores, DataBase db) throws SQLException {

        String sql = "SELECT * from grupo_empresa e where e.grupo = " + "'" + grupo_empresa[1] + "' AND e.empresa = '" + grupo_empresa[0] + "';";
        ResultSet res = db.consulta(sql);

        if (res.getString(1) == null) {
            listErrores.add(new ErrorTransaccion(index, msj.grupoEmpresaNoExiste()));
        }
    }

    /**
     * Verifica el legajo. Primero verifica la longitud que cumpla con los 13
     * caracteres Despues descompone el legajo para verificar el tipo y el
     * numero de documento si estan dentro de un rango
     *
     * @param legajo
     * @param index
     * @param listErrores
     */
    public static void verificarLegajo(String legajo, int index, List<ErrorTransaccion> listErrores) {

        if (esNumerico(legajo)) {
            // SE VERIFICA LA LONGITUD 
            if (legajo.length() > LONGITUD_LEGAJO) {
                listErrores.add(new ErrorTransaccion(index, msj.longitud_Legajo_Mayor()));
            } else {
                if (legajo.length() < LONGITUD_LEGAJO) {
                    listErrores.add(new ErrorTransaccion(index, msj.longitud_Legajo_Menor()));
                } else {
                    //SE VERIFICA LA DESCOMPOSICION DEL LEGAJO
                    descomponerLegajo(legajo, index, listErrores);
                }
            }
        } else {
            listErrores.add(new ErrorTransaccion(index, msj.formatoInvalido_Numerico("legajo")));
        }
    }

    private static void descomponerLegajo(String legajo, int index, List<ErrorTransaccion> listErrores) {

        int tipo_doc = Integer.parseInt(legajo.substring(0, 3)); //100 o 200 o 500 
        int num_doc = Integer.parseInt(legajo.substring(3, 11)); //11193009
//        int sec_doc = Integer.parseInt(legajo.substring(11, 13)); //11193009

        boolean encontro = Arrays.stream(TIPOS_DOCUMENTO).boxed().collect(Collectors.toSet()).contains(tipo_doc);
//        boolean encontros = IntStream.of(TIPOS_DOCUMENTO).anyMatch(element -> element == tipo_doc);

        if (!encontro) {
            listErrores.add(new ErrorTransaccion(index, msj.tipoDocumentoNoExiste()));
        }

        if (num_doc > MAXIMO_DOCUMENTO) {
            listErrores.add(new ErrorTransaccion(index, msj.numeroDocumento_Mayor()));
        }
    }

    /**
     * Encargado de verificar el Codigo del concepto
     *
     * @param codigoConcepto
     * @param index
     * @param listErrores
     */
    public static void verificarCodigoConcepto(String codigoConcepto, int index, List<ErrorTransaccion> listErrores) {

        if (esNumerico(codigoConcepto)) {
            if (codigoConcepto.length() > LONGITUD_CODIGO_CONCEPTO || codigoConcepto == null) {
                listErrores.add(new ErrorTransaccion(index, msj.codigoConceptoLongitudMayor()));
            } else {
                if (codigoConcepto.length() < LONGITUD_CODIGO_CONCEPTO) {
                    listErrores.add(new ErrorTransaccion(index, msj.codigoConceptoLongitudMenor()));
                }
            }
        } else {
            listErrores.add(new ErrorTransaccion(index, msj.formatoInvalido_Numerico("concepto")));
        }
    }

    public static void verificarDescripcionConcepto(String descripcionConcepto, int index, List<ErrorTransaccion> listErrores) {

        if (descripcionConcepto.length() > LONGITUD_DESCRIPCION_CONCEPTO) {
            listErrores.add(new ErrorTransaccion(index, msj.descripcionConceptoLongitudMayor()));
        } else if (descripcionConcepto.length() == 0) {
            listErrores.add(new ErrorTransaccion(index, msj.descipcionVacia()));
        }
    }

    public static void verificarCantidad(String cantidad, int index, List<ErrorTransaccion> listErrores) {

        //con esto se cubren los negativos
        String numero_sinSigno = cantidad.replace("-", "");
        String numero_sinSigno_sincomas = numero_sinSigno.replace(",", "");
        //Verifico que mi string sin coma y sin signo sean solo numeros
        if (esNumerico(numero_sinSigno_sincomas)) {
            //no encontre otro nombre mas representativo xd
            verificarSignoComaCantidad(numero_sinSigno, cantidad, index, listErrores);
        } else {
            listErrores.add(new ErrorTransaccion(index, msj.formatoInvalido_Numerico("cantidad")));
        }
    }

    public static void verificarImporte(String importe, int index, List<ErrorTransaccion> listErrores) {

        if (!esCantidadCero) {
            //Se setea en falso en caso de que el siguiente mi importe sea negativo
            esCantidadCero = false;
            esImporteNegativo = esNegativo(importe);

            //devuelve TRUE cuando son distintos signos
            if (esCantidadNegativa ^ esImporteNegativo) {
                listErrores.add(new ErrorTransaccion(index, msj.signosImporteCantidadDistintos()));
            }

            //se tiene en cuenta a los signos y puntos?
            int cant = importe.length();
            if (importe.length() > LONGITUD_IMPORTE) {
                listErrores.add(new ErrorTransaccion(index, msj.importeLongitudMayor()));
            }
        }
    }

    public static boolean esNegativo(String cantidad) {
        return "-".equals(cantidad.substring(0, 1));
    }

    public static void verificarFecha(String fecha, int index, List<ErrorTransaccion> listErrores) {

        if (esNumerico(fecha)) {
            if (fecha.length() > LONGITUD_FECHA) {
                listErrores.add(new ErrorTransaccion(index, msj.fechaLongitudMayor()));
            } else {
                if (fecha.length() < LONGITUD_FECHA) {
                    listErrores.add(new ErrorTransaccion(index, msj.fechaLongitudMenor()));
                } else {
                    try {
                        descomponerFecha(fecha, index, listErrores);

                    } catch (ParseException ex) {
                        Logger.getLogger(Verifica.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            listErrores.add(new ErrorTransaccion(index, msj.formatoInvalido_Numerico("fecha")));
        }
    }

    private static void descomponerFecha(String fecha, int index, List<ErrorTransaccion> listErrores) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaCsv = simpleDateFormat.parse(obtenerFechaArchivo(fecha, index, listErrores));
        //obtengo mi fecha actual        
        Date fechaActual = new Date(System.currentTimeMillis());

    }

    private static String obtenerFechaArchivo(String fecha, int index, List<ErrorTransaccion> listErrores) {

        String anio = fecha.substring(0, 4);  //050 
        String mes = fecha.substring(4, 6);  //050 
        String dia = fecha.substring(6, 8);  //050
        boolean fechaCorrecta = true;

        Calendar FActual = new GregorianCalendar();
        int anioActual = FActual.get(Calendar.YEAR);
        int mesActual = FActual.get(Calendar.MONTH);

        if (Integer.parseInt(anio) < anioActual) {
            NotificacionList.getInstance().agregar(msj.añoAnterior(index));
        }

        if ((Integer.parseInt(mes) > mesActual) && (anioActual > Integer.parseInt(anio))) {
            NotificacionList.getInstance().agregar(msj.fechaMayorActual(index));

        }

        if (Integer.parseInt(mes) > MAX_MES) {
            listErrores.add(new ErrorTransaccion(index, msj.noEsMes()));
            fechaCorrecta = false;
        }

        if (Integer.parseInt(dia) > MAX_MES) {
            listErrores.add(new ErrorTransaccion(index, msj.noEsDia()));
            fechaCorrecta = false;
        }
        return anio + "-" + mes + "-" + dia; //fecha de ejemplo 2023-02-05

    }

    private static boolean esNumerico(String legajo) {
        // Utiliza el método matches de una expresión regular para verificar si la cadena contiene solo números
        return legajo.matches("\\d+");
    }

    private static void verificarSignoComaCantidad(String numero_sinSigno, String cantidad, int index, List<ErrorTransaccion> listErrores) {
        String subcadena;
        int longitud;

        // Verificar si el string contiene una coma
        if (numero_sinSigno.contains(",")) {
            subcadena = numero_sinSigno.substring(0, cantidad.indexOf(','));
            longitud = subcadena.length() - 1;
        } else {
            //no tiene coma, por lo tanto tomo todo
            subcadena = numero_sinSigno;
            longitud = subcadena.length();
        }

        if (longitud <= LONGITUD_CANTIDAD) {
            esCantidadNegativa = esNegativo(cantidad);
            if (!esCantidadNegativa) {
                esCantidadCero = cantidad.equals("0");
            }
        } else {
            listErrores.add(new ErrorTransaccion(index, msj.cantidadLongitudMayor()));
        }
    }
}
