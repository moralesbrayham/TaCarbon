/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.example;

// DTOs (ajusta el paquete según corresponda)

// Timer para refresco automático
import javax.swing.Timer;

// Layout

// HTTP y JSON
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;

// JSON (Gson)
import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;

// Listas y estructuras
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;



/**
 *
 * @author moral
 */
public class PantallaCocina extends javax.swing.JFrame {
    private Timer actualizador;


    /**
     * Creates new form PantallaCocina
     */
    public PantallaCocina() {
        initComponents();
        panelOrdenes.setLayout(new BoxLayout(panelOrdenes, BoxLayout.Y_AXIS));
        iniciarActualizacion();
    }
    
    private void iniciarActualizacion() {
        actualizador = new Timer(3000, e -> cargarOrdenes()); // actualiza cada 3 segundos
        actualizador.start();
    }
    
    private void cargarOrdenes() {
        try {
            URL url = new URL("http://localhost:8080/api/ventas/cocina");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String response = br.lines().collect(Collectors.joining());
                    actualizarPanelOrdenes(response);
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            System.out.println("Error al cargar órdenes: " + e.getMessage());
        }
    }
    
    private void actualizarPanelOrdenes(String jsonResponse) {

        panelOrdenes.removeAll();
        Gson gson = new Gson();

        List<Map<String, Object>> detalles = gson.fromJson(jsonResponse, List.class);

        if (detalles == null || detalles.isEmpty()) {
            panelOrdenes.revalidate();
            panelOrdenes.repaint();
            return;
        }

        // 🔥 1. Agrupar por ventaId
        Map<Double, List<Map<String, Object>>> agrupadas = detalles.stream()
            .filter(d -> d.get("ventaId") != null)
            .collect(Collectors.groupingBy(d -> (Double) d.get("ventaId")));

        // 🔥 2. Orden FIFO (más antiguas primero)
        List<Double> ventasOrdenadas = agrupadas.keySet().stream()
            .sorted()
            .limit(8) // máximo 8 órdenes
            .collect(Collectors.toList());

        // 🔥 3. Invertir para UI (más nueva arriba izquierda)
        Collections.reverse(ventasOrdenadas);

        // 🔥 Layout 2x4
        panelOrdenes.setLayout(new GridLayout(2, 4, 10, 10));

        // 🔥 4. Construir tarjetas
        for (Double ventaId : ventasOrdenadas) {

            List<Map<String, Object>> lista = agrupadas.get(ventaId);

            if (lista == null || lista.isEmpty()) continue;

            Map<String, Object> primerDetalle = lista.get(0);

            int mesa = primerDetalle.get("numeroMesa") != null
                    ? ((Double) primerDetalle.get("numeroMesa")).intValue()
                    : 0;

            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createTitledBorder(
                "Orden #" + ventaId.intValue() + " - Mesa " + mesa
            ));

            StringBuilder detallesTexto = new StringBuilder();

            // 🔥 5. Recorrer productos
            for (Map<String, Object> d : lista) {

                String nombre = d.get("nombreProducto") != null
                        ? d.get("nombreProducto").toString()
                        : "Desconocido";

                int cantidad = d.get("cantidad") != null
                        ? ((Double) d.get("cantidad")).intValue()
                        : 0;

                String nota = d.get("nota") != null
                        ? d.get("nota").toString()
                        : "";

                int suborden = d.get("suborden") != null
                        ? ((Double) d.get("suborden")).intValue()
                        : 0;

                detallesTexto.append("P").append(suborden)
                    .append(": ")
                    .append(nombre)
                    .append(" x")
                    .append(cantidad);

                if (!nota.isEmpty()) {
                    detallesTexto.append(" (").append(nota).append(")");
                }

                detallesTexto.append("\n");
            }

            JTextArea area = new JTextArea(detallesTexto.toString());
            area.setFont(new Font("Arial", Font.PLAIN, 16));
            area.setEditable(false);

            // 🔥 6. Estado (tomamos el primero como referencia)
            String estado = lista.get(0).get("estadoCocina") != null
                    ? lista.get(0).get("estadoCocina").toString()
                    : "EN_ESPERA";

            JButton btnEstado = new JButton(estado);

            // 🔥 Acción botón
            btnEstado.addActionListener(e -> cambiarEstadoDetalle(lista, btnEstado));

            // 🔥 Estilo botón
            btnEstado.setFont(new Font("Arial", Font.BOLD, 14));
            btnEstado.setForeground(Color.WHITE);

            switch (estado) {
                case "EN_ESPERA":
                    btnEstado.setBackground(Color.ORANGE);
                    break;
                case "EN_PREPARACION":
                    btnEstado.setBackground(Color.BLUE);
                    break;
                case "LISTO":
                    btnEstado.setBackground(Color.GREEN.darker());
                    break;
                default:
                    btnEstado.setBackground(Color.GRAY);
            }

            JPanel bottom = new JPanel(new FlowLayout());
            bottom.add(btnEstado);

            card.add(area, BorderLayout.CENTER);
            card.add(bottom, BorderLayout.SOUTH);

            panelOrdenes.add(card);
        }

        panelOrdenes.revalidate();
        panelOrdenes.repaint();
    }

    
    private void cambiarEstadoDetalle(List<Map<String, Object>> detalles, JButton boton) {

        String estadoActual = boton.getText();
        String nuevoEstado;

        switch (estadoActual) {
            case "EN_ESPERA":
                nuevoEstado = "EN_PREPARACION";
                break;
            case "EN_PREPARACION":
                nuevoEstado = "LISTO";
                break;
            default:
                return;
        }

        try {

            for (Map<String, Object> d : detalles) {

                int id = ((Double) d.get("id")).intValue();

                URL url = new URL("http://localhost:8080/api/ventas/detalle/" + id + "/estado");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String body = "{\"estadoCocina\":\"" + nuevoEstado + "\"}";

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes("utf-8"));
                }

                conn.getResponseCode();
                conn.disconnect();
            }

            boton.setText(nuevoEstado);

        } catch (Exception e) {
            System.out.println("Error al cambiar estado: " + e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        btnMenuPrincipal = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelOrdenes = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Punto de Venta TaCarbon - Pantalla de cocina");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));

        btnMenuPrincipal.setBackground(new java.awt.Color(220, 220, 220));
        btnMenuPrincipal.setFont(new java.awt.Font("Bell MT", 1, 18)); // NOI18N
        btnMenuPrincipal.setText("Menu Principal");
        btnMenuPrincipal.setBorder(new javax.swing.border.MatteBorder(null));
        btnMenuPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuPrincipalActionPerformed(evt);
            }
        });

        panelOrdenes.setFont(new java.awt.Font("Bodoni MT", 0, 18)); // NOI18N

        javax.swing.GroupLayout panelOrdenesLayout = new javax.swing.GroupLayout(panelOrdenes);
        panelOrdenes.setLayout(panelOrdenesLayout);
        panelOrdenesLayout.setHorizontalGroup(
            panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 798, Short.MAX_VALUE)
        );
        panelOrdenesLayout.setVerticalGroup(
            panelOrdenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 542, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelOrdenes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnMenuPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnMenuPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    
    private void btnMenuPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuPrincipalActionPerformed
        // TODO add your handling code here:
    
        // Open the MainPOSWindow
        MainPOSWindow mainWindow = new MainPOSWindow();
        mainWindow.setVisible(true);  // Show the MainPOSWindow
    }//GEN-LAST:event_btnMenuPrincipalActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PantallaCocina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PantallaCocina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PantallaCocina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PantallaCocina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PantallaCocina().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMenuPrincipal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelOrdenes;
    // End of variables declaration//GEN-END:variables
}
