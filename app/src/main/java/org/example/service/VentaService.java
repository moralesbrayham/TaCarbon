package org.example.service;

import org.example.model.Venta;
import org.example.model.DetalleVenta;
import org.example.model.Producto;
import org.example.repository.VentaRepository;
import org.example.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Obtener todas las ventas
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    // Obtener una venta por ID
    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    // Registrar una nueva venta
    @Transactional
    public Venta realizarVenta(Venta venta) {
        venta.setFecha(LocalDateTime.now()); // Ajustar fecha/tiempo actual
        double totalVenta = 0;

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Restar la cantidad vendida del stock disponible
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            // Asociar el producto y calcular subtotal
            detalle.setProducto(producto);
            detalle.setSubtotal(producto.getPrecio() * detalle.getCantidad());
            detalle.setVenta(venta);  // Asociar el detalle con la venta

            totalVenta += detalle.getSubtotal();
        }

        // Setear total de la venta
        venta.setTotal(totalVenta);

        // Guardar la venta (cascada también guardará los detalles)
        Venta nuevaVenta = ventaRepository.save(venta);

        // Generar ticket en PDF
        TicketService ticketService = new TicketService();
        String ticketPath = ticketService.generarTicketPDF(nuevaVenta);
        System.out.println("Ticket generado en: " + ticketPath);

        return nuevaVenta;

    }

    // Eliminar una venta
    public void eliminarVenta(Long id) {
        if (ventaRepository.existsById(id)) {
            ventaRepository.deleteById(id);
        }
    }
}



