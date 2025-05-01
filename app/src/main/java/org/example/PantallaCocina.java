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
            URL url = new URL("http://localhost:8080/api/ventas/pendientes");
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
    List<Map<String, Object>> ordenes = gson.fromJson(jsonResponse, List.class);

    // Ordenar por ID (de menor a mayor, más antiguas primero)
    ordenes.sort(Comparator.comparing(o -> ((Double) o.get("id"))));

    // Tomar las últimas 8 órdenes (las más recientes al final)
    // Filtrar solo las órdenes activas (en espera o en preparación)
    List<Map<String, Object>> ordenesActivas = ordenes.stream()
        .filter(o -> {
            String estado = (String) o.get("estado");
            return estado.equals("En espera") || estado.equals("En preparación");
        })
        .limit(8) // Solo las primeras 8 activas
        .collect(Collectors.toList());

    ordenes = ordenesActivas;


    // Invertir para que la más nueva esté arriba a la izquierda y la más vieja abajo a la derecha
    Collections.reverse(ordenes);

    panelOrdenes.setLayout(new GridLayout(2, 4, 10, 10)); // 2 filas, 4 columnas, con espacios

    for (Map<String, Object> orden : ordenes) {
        int id = ((Double) orden.get("id")).intValue();
        int mesa = ((Double) orden.get("numeroMesa")).intValue();
        String estado = (String) orden.get("estado");

        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createTitledBorder("Orden #" + id + " - Mesa " + mesa));

        // Procesar productos
        List<Map<String, Object>> productos = (List<Map<String, Object>>) orden.get("productos");
        StringBuilder detalles = new StringBuilder();

        for (Map<String, Object> producto : productos) {
            String nombre = producto.get("nombreProducto") != null ? producto.get("nombreProducto").toString() : "Desconocido";
            int cantidad = 0;
            if (producto.get("cantidad") instanceof Number) {
                cantidad = ((Number) producto.get("cantidad")).intValue();
                
            }


            Object subordenObj = producto.get("suborden");
            String suborden = "N/A";
            if (subordenObj instanceof Number) {
            suborden = String.valueOf(((Number) subordenObj).intValue());
            }

            detalles.append("Plato ").append(suborden).append(": ")
            .append(nombre).append(" x").append(cantidad).append("\n");


        }


        JTextArea areaDetalles = new JTextArea(detalles.toString());
        areaDetalles.setFont(new Font("Arial", Font.PLAIN, 18));
        areaDetalles.setEditable(false);

        JButton btnEstado = new JButton(estado);
        btnEstado.setPreferredSize(new Dimension(150, 30));
        btnEstado.addActionListener(e -> cambiarEstado(id, btnEstado));

        // Estilo de botón
        btnEstado.setFont(new Font("Arial", Font.BOLD, 16));
        btnEstado.setForeground(Color.WHITE);
        switch (estado) {
            case "En espera":
                btnEstado.setBackground(Color.ORANGE);
                break;
            case "En preparación":
                btnEstado.setBackground(Color.BLUE);
                break;
            case "Orden lista":
                btnEstado.setBackground(Color.GREEN.darker());
                break;
            default:
                btnEstado.setBackground(Color.GRAY);
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnEstado);

        card.add(areaDetalles, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        panelOrdenes.add(card);
    }

    panelOrdenes.revalidate();
    panelOrdenes.repaint();
}

    
    private void cambiarEstado(int idOrden, JButton boton) {
        String estadoActual = boton.getText();
        String nuevoEstado;

        // Lógica para decidir el siguiente estado
        switch (estadoActual) {
            case "En espera":
                nuevoEstado = "En preparación";
                break;
            case "En preparación":
                nuevoEstado = "Orden lista";
                break;
            case "Orden lista":
                // Ya está lista, no hacer nada o volver a "En espera" si quieres ciclo
                return;
            default:
                nuevoEstado = "En espera";
    }

        try {
            URL url = new URL("http://localhost:8080/api/ventas/" + idOrden + "/estado");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String cuerpo = "{\"estado\":\"" + nuevoEstado + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(cuerpo.getBytes("utf-8"));
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                boton.setText(nuevoEstado);

                // Estilo del botón actualizado
                boton.setFont(new Font("Arial", Font.BOLD, 16));
                boton.setForeground(Color.WHITE);

                switch (nuevoEstado) {
                    case "En espera":
                        boton.setBackground(Color.ORANGE);
                        break;
                    case "En preparación":
                        boton.setBackground(Color.BLUE);
                        break;
                    case "Orden lista":
                        boton.setBackground(Color.GREEN.darker());
                        JOptionPane.showMessageDialog(this, "¡Orden #" + idOrden + " está lista!");
                        break;
                    default:
                        boton.setBackground(Color.GRAY);
                }
            }

            conn.disconnect();
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
