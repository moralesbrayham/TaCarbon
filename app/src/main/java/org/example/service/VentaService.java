package org.example.service;

import org.example.model.Venta;
import org.example.model.DetalleVenta;
import org.example.model.Producto;
import org.example.repository.VentaRepository;
import org.example.repository.ProductoRepository;
import org.example.dto.VentaDTO;
import org.example.dto.ItemDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaService {
    
    @Autowired
    private  VentaRepository ventaRepository;

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
        // Set default status
        venta.setEstado("En espera");
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

    @Transactional
    public void eliminarVenta(Long id) {
     Venta venta = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        // Restaurar stock por cada producto vendido
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        // Cambiar estado a "Eliminada"
        venta.setEstado("Eliminada");
        ventaRepository.save(venta);

        // Generar ticket de venta eliminada
     TicketService ticketService = new TicketService();
     String path = ticketService.generarTicketEliminadoPDF(venta);
        System.out.println("Ticket de venta eliminada generado en: " + path);
    }

    
    public List<VentaDTO> obtenerOrdenesPendientes() {
        List<String> estadosPermitidos = List.of("En espera", "En preparación");
        List<Venta> ventas = ventaRepository.findByEstadoIn(estadosPermitidos);

        return ventas.stream().map(venta -> {
            VentaDTO dto = new VentaDTO();
            dto.setId(venta.getId());
            dto.setNumeroMesa(venta.getNumeroMesa());
            dto.setEstado(venta.getEstado());

            List<ItemDTO> items = venta.getDetalles().stream().map(detalle -> {
                ItemDTO item = new ItemDTO();
                item.setNombreProducto(detalle.getProducto().getNombre());
                item.setCantidad(detalle.getCantidad());
                item.setSuborden(detalle.getSuborden() != null ? detalle.getSuborden() : 0);
                return item;
            }).collect(Collectors.toList());

            dto.setProductos(items);
            return dto;
        }).collect(Collectors.toList());
    }

    
    public void actualizarEstado(Long idVenta, String nuevoEstado) {
        Optional<Venta> ventaOptional = ventaRepository.findById(idVenta);

        if (ventaOptional.isPresent()) {
            Venta venta = ventaOptional.get();
            venta.setEstado(nuevoEstado);
            ventaRepository.save(venta);
        } else {
            throw new RuntimeException("Venta no encontrada con ID: " + idVenta);
        }
    }

    
}