package interfaz.grafica;

import com.mycompany.transacciones.DataBase;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author dgc06
 */
public final class SesionUsuario extends javax.swing.JFrame {

    /**
     * Creates new form SesionUsuario
     */
    DataBase db = null;

    public SesionUsuario(DataBase db) {
        this.db = db;
        initComponents();
        try {
            cargarDatoSession();
            cargarImagenes();
        } catch (SQLException ex) {
            Logger.getLogger(SesionUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarDatoSession() throws SQLException {
        ResultSet rs = db.consulta("SELECT * FROM fecha_creacion");
        while (rs.next()) {
            tipoArchivo.setText("Tipo de archivo:   "+(String) rs.getObject(1));
            rutaAbsoluta.setText("Ubicacion:   "+(String) rs.getObject(3));
            fechaSubida.setText("Fecha de carga:   "+(String) rs.getObject(2));
            File archivo = new File((String) rs.getObject(3));
            nombreArchivo.setText("Nombre de Archivo   "+archivo.getName());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        javax.swing.JButton jButton1 = new javax.swing.JButton();
        javax.swing.JButton jButton2 = new javax.swing.JButton();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        rutaAbsoluta = new javax.swing.JLabel();
        nombreArchivo = new javax.swing.JLabel();
        fechaSubida = new javax.swing.JLabel();
        tipoArchivo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setBackground(new java.awt.Color(0, 102, 204));
        jButton1.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("SI");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.setBorderPainted(false);
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 450, 130, 40));

        jButton2.setBackground(new java.awt.Color(0, 102, 204));
        jButton2.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("NO");
        jButton2.setBorder(null);
        jButton2.setBorderPainted(false);
        jButton2.setFocusPainted(false);
        jButton2.setFocusable(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 450, 130, 40));

        jLabel1.setFont(new java.awt.Font("Roboto Light", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("¿Desea continuar con el archivo cargado?");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 400, 340, 30));

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Posees el siguiente archivo cargado en el sistema");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 410, 40));
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 90, 70, 80));

        rutaAbsoluta.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        rutaAbsoluta.setText("rutaAbsoluta");
        jPanel1.add(rutaAbsoluta, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 320, 390, 20));

        nombreArchivo.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        nombreArchivo.setText("nombreArchivo");
        jPanel1.add(nombreArchivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, 340, -1));

        fechaSubida.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        fechaSubida.setText("fecha_subida:");
        jPanel1.add(fechaSubida, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 280, 390, -1));

        tipoArchivo.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tipoArchivo.setText("tipoArchivo");
        jPanel1.add(tipoArchivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 240, 390, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 670, 530));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            db.cleanDB();
            new tipoArchivo(db).setVisible(true);
//            new FormInicio(db).setVisible(true);
            dispose();
        } catch (SQLException ex) {
            Logger.getLogger(SesionUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

//        Loading load = new Loading();
//        load.setVisible(true);

        try {
            this.setVisible(false);
            new TablaIU(db).setVisible(true);
//            load.setVisible(false);
        } catch (SQLException ex) {
            Logger.getLogger(SesionUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel fechaSubida;
    javax.swing.JLabel jLabel4;
    javax.swing.JPanel jPanel1;
    javax.swing.JLabel nombreArchivo;
    javax.swing.JLabel rutaAbsoluta;
    javax.swing.JLabel tipoArchivo;
    // End of variables declaration//GEN-END:variables

    private void cargarImagenes() {
        String dirActual = System.getProperty("user.dir");
        jLabel4.setIcon(new javax.swing.ImageIcon(dirActual + "\\images\\doc60px.png"));
        try {
            BufferedImage iconImage = ImageIO.read(new File(dirActual+"\\images\\transaccion.png"));
            
            // Establecer la imagen como ícono de la aplicación
            this.setIconImage(iconImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
