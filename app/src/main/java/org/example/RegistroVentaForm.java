/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.example;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import org.example.service.ProductoService;
import org.example.service.VentaService; // Adjust if it's in another package
import org.example.model.Venta;
import org.example.model.DetalleVenta;
import org.example.model.Producto;
import org.example.repository.ProductoRepository;



/**
 *
 * @author moral
 */
public class RegistroVentaForm extends javax.swing.JFrame {

    /**
     * Creates new form RegistroVentaForm
     */
    private VentaService ventaService; // Declare the service

     @Autowired
private ProductoRepository productoRepository;
     
     

    public RegistroVentaForm() {
        
        initComponents();
        ventaService = new VentaService(); // Initialize the service
        subordenes.add(1); // Ensure Suborden 1 is always in the list
        currentSuborden = 1; // Set it as the active Suborden
        cargarProductos();
        
    try {
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tacarbon_db", "root", "m1o3r5a7");
    ventaService = new VentaService();
    } catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage());
    }
        
}
    
    
    private List<Integer> subordenes = new ArrayList<>();
    private int currentSuborden = 1; // Keeps track of the current suborden


            private void cargarProductos() {
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tacarbon_db", "root", "m1o3r5a7");
        String sql = "SELECT nombre FROM productos";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        cbxProductos.removeAllItems(); // Limpiar antes de agregar

        while (rs.next()) {
            cbxProductos.addItem(rs.getString("nombre"));
        }

        rs.close();
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        btnFinalizarVenta = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNumeroMesa = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSubordenes = new javax.swing.JTable();
        btnEliminarSuborden = new javax.swing.JButton();
        cbxProductos = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        btnAgregarProducto = new javax.swing.JButton();
        btnNuevaSuborden = new javax.swing.JButton();
        btnListaSubordenes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 500));

        btnFinalizarVenta.setBackground(new java.awt.Color(0, 209, 59));
        btnFinalizarVenta.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 18)); // NOI18N
        btnFinalizarVenta.setText("Finalizar venta");
        btnFinalizarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarVentaActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Yu Gothic Medium", 3, 36)); // NOI18N
        jLabel1.setText("Registro de venta");

        jButton3.setBackground(new java.awt.Color(255, 0, 0));
        jButton3.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 18)); // NOI18N
        jButton3.setText("Cancelar");

        jLabel2.setFont(new java.awt.Font("Yu Gothic Medium", 1, 18)); // NOI18N
        jLabel2.setText("Total:");

        lblTotal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(33, 172, 25), new java.awt.Color(0, 204, 0), new java.awt.Color(0, 204, 51), new java.awt.Color(0, 204, 51)));

        jLabel3.setBackground(new java.awt.Color(242, 94, 68));
        jLabel3.setFont(new java.awt.Font("Yu Gothic Light", 1, 14)); // NOI18N
        jLabel3.setText("Numero de mesa:");

        tblSubordenes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Id Suborden", "Producto", "Cantidad", "Precio", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        // ✅ Add a TableModelListener after initializing the table
        tblSubordenes.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int fila = e.getFirstRow();
                    int columna = e.getColumn();

                    if (columna == 3) { // ✅ Corrected column index for "Cantidad"
                        actualizarSubtotal(fila);
                        actualizarTotal();
                    }
                }
            }
        });
        jScrollPane1.setViewportView(tblSubordenes);

        jScrollPane2.setViewportView(jScrollPane1);

        btnEliminarSuborden.setText("Eliminar Suborden");
        btnEliminarSuborden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarSubordenActionPerformed(evt);
            }
        });

        cbxProductos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setBackground(new java.awt.Color(242, 94, 68));
        jLabel4.setFont(new java.awt.Font("Yu Gothic Light", 1, 14)); // NOI18N
        jLabel4.setText("Cantidad");

        btnAgregarProducto.setText("Agregar Producto");
        btnAgregarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProductoActionPerformed(evt);
            }
        });

        btnNuevaSuborden.setText("Nueva Suborden");
        btnNuevaSuborden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaSubordenActionPerformed(evt);
            }
        });

        btnListaSubordenes.setText("Lista Subordenes");
        btnListaSubordenes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListaSubordenesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(btnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNumeroMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(txtCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                            .addComponent(cbxProductos, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(btnNuevaSuborden, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarSuborden, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnListaSubordenes, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(87, 87, 87)
                                .addComponent(btnFinalizarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(180, 180, 180)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(160, 160, 160))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumeroMesa))
                        .addGap(18, 18, 18)
                        .addComponent(cbxProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminarSuborden)
                    .addComponent(btnNuevaSuborden))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnListaSubordenes)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFinalizarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductoActionPerformed
        // TODO add your handling code here:
                    DefaultTableModel modeloTabla = (DefaultTableModel) tblSubordenes.getModel();
                    

    String productoSeleccionado = (String) cbxProductos.getSelectedItem();
    String cantidadStr = txtCantidad.getText();

    if (productoSeleccionado != null && !cantidadStr.trim().isEmpty()) {
        try {
            int cantidad = Integer.parseInt(cantidadStr);

            // Obtener precio del producto desde la base de datos
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tacarbon_db", "root", "m1o3r5a7");
            String sql = "SELECT  id, precio FROM productos WHERE nombre = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productoSeleccionado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long productoId = rs.getLong("id");  // Obtener el ID del producto
                double precio = rs.getDouble("precio");
                double total = cantidad * precio;


            // Agregar a la tabla con el ID antes de currentSuborden
            modeloTabla.addRow(new Object[]{productoId, currentSuborden, productoSeleccionado, cantidad, precio, total});


            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Seleccione un producto e ingrese la cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
    }
        // (Opcional) Si tienes un total, actualizarlo
        actualizarTotal();
    }//GEN-LAST:event_btnAgregarProductoActionPerformed

    private void btnNuevaSubordenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaSubordenActionPerformed
        // TODO add your handling code here:
    currentSuborden++; //Increment before adding
    subordenes.add(currentSuborden);
    JOptionPane.showMessageDialog(this, "Suborden " + currentSuborden + " Creada");
    }//GEN-LAST:event_btnNuevaSubordenActionPerformed

private void actualizarSubtotal(int fila) {
    DefaultTableModel modelo = (DefaultTableModel) tblSubordenes.getModel();
    
    try {
        // ✅ Validate Cantidad is a valid number
        Object cantidadObj = modelo.getValueAt(fila, 3);
        if (cantidadObj == null || cantidadObj.toString().trim().isEmpty()) {
            return; // Do nothing if Cantidad is empty
        }
        int cantidad = Integer.parseInt(cantidadObj.toString());

        // ✅ Get Precio (either from the table or from the database)
        double precio;
        Object precioObj = modelo.getValueAt(fila, 4);
        
        if (precioObj == null) {
            // If precio is missing, fetch it from the database
            String productoSeleccionado = modelo.getValueAt(fila, 2).toString(); // Product name
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tacarbon_db", "root", "m1o3r5a7");
            String sql = "SELECT precio FROM productos WHERE nombre = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productoSeleccionado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                precio = rs.getDouble("precio");
                modelo.setValueAt(precio, fila, 4); // ✅ Save precio in table
            } else {
                throw new Exception("Precio no encontrado para " + productoSeleccionado);
            }

            conn.close();
        } else {
            precio = Double.parseDouble(precioObj.toString());
        }

        // ✅ Calculate new subtotal
        double nuevoSubtotal = cantidad * precio;
        modelo.setValueAt(nuevoSubtotal, fila, 5); // ✅ Update subtotal column (5)

        actualizarTotal(); // ✅ Update total
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Ingrese un número válido en Cantidad", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al actualizar subtotal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    private void actualizarTotal() {
    DefaultTableModel model = (DefaultTableModel) tblSubordenes.getModel();
    double total = 0.0;

    // Suponiendo que la columna del precio total está en el índice 5
    for (int i = 0; i < model.getRowCount(); i++) {
        total += Double.parseDouble(model.getValueAt(i, 5).toString());
    }

    lblTotal.setText(String.format("$ %.2f", total)); // Muestra el total en un JLabel
}

    private void btnEliminarSubordenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarSubordenActionPerformed
        // TODO add your handling code here:
            // Obtener el modelo de la tabla
    DefaultTableModel model = (DefaultTableModel) tblSubordenes.getModel();
    
    // Obtener la fila seleccionada
    int selectedRow = tblSubordenes.getSelectedRow();
    
    // Verificar si se ha seleccionado una fila
    if (selectedRow != -1) {
        // Eliminar la fila seleccionada
        model.removeRow(selectedRow);
        
        // (Opcional) Si tienes un total, actualizarlo
        actualizarTotal();
    } else {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona una suborden para eliminar.");
    }
    }//GEN-LAST:event_btnEliminarSubordenActionPerformed

    private void btnListaSubordenesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListaSubordenesActionPerformed
        // TODO add your handling code here:
        
            if (subordenes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay Subórdenes creadas.");
        return;
    }

    // Convert list to array for JOptionPane
    String[] opciones = subordenes.stream().map(s -> "Suborden " + s).toArray(String[]::new);

    // Show selection dialog
    String seleccion = (String) JOptionPane.showInputDialog(this, 
            "Seleccione una Suborden:", "Lista de Subórdenes",
            JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

    // If a Suborden was selected, switch to it
    if (seleccion != null) {
        currentSuborden = Integer.parseInt(seleccion.split(" ")[1]);
        JOptionPane.showMessageDialog(this, "Ahora estás en " + seleccion);
    }
    }//GEN-LAST:event_btnListaSubordenesActionPerformed

    private void btnFinalizarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarVentaActionPerformed
     // implement all handling code here
        try {
            // Get the table number from the text field
            String numeroMesaText = txtNumeroMesa.getText().trim();
            if (numeroMesaText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El número de mesa no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int numeroMesa = Integer.parseInt(numeroMesaText);

            // Create the Venta object and set the mesa number
            Venta venta = new Venta();
            venta.setNumeroMesa(numeroMesa);  // Set the table number

            // Prepare the details list from the table (tblsubordenes)
            List<DetalleVenta> detalles = new ArrayList<>();
            for (int row = 0; row < tblSubordenes.getRowCount(); row++) {
                // Assuming the table columns are: "Id", "Producto", "Cantidad", "Precio", "Subtotal"
                int idProducto = Integer.parseInt(tblSubordenes.getValueAt(row, 0).toString()); // ID from the table
                int cantidad = Integer.parseInt(tblSubordenes.getValueAt(row, 3).toString()); // Quantity from the table

                // Create DetalleVenta and set the Producto and cantidad
                DetalleVenta detalle = new DetalleVenta();
                
                // Fetch the product by its ID
                Producto producto = productoRepository.findById(Long.valueOf(idProducto))
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + idProducto));
                
                // Set the id and cantidad
                detalle.setProducto(producto);
                detalle.setId(Long.valueOf(idProducto));
                detalle.setCantidad(cantidad);

                // Add the DetalleVenta to the list
                detalles.add(detalle);
            }

            // Set the detalles to the Venta object
            venta.setDetalles(detalles);

            // Call the realizarVenta() method to process the sale
            Venta nuevaVenta = ventaService.realizarVenta(venta);

            // Optionally, clear the form or reset fields
            txtNumeroMesa.setText(""); // Clear the mesa number
            // Reset table or other form elements as needed
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido para la cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar la venta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnFinalizarVentaActionPerformed


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
                new RegistroVentaForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarProducto;
    private javax.swing.JButton btnEliminarSuborden;
    private javax.swing.JButton btnFinalizarVenta;
    private javax.swing.JButton btnListaSubordenes;
    private javax.swing.JButton btnNuevaSuborden;
    private javax.swing.JComboBox<String> cbxProductos;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblSubordenes;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtNumeroMesa;
    // End of variables declaration//GEN-END:variables
}


