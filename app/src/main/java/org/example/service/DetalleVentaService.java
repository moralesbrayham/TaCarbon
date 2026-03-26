/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.service;

import org.example.model.DetalleVenta;
import org.example.model.Producto;
import org.example.model.Venta;
import org.example.repository.DetalleVentaRepository;
import org.example.repository.ProductoRepository;
import org.example.repository.VentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public DetalleVenta agregarProducto(
            Long ventaId,
            Long productoId,
            Integer cantidad,
            Double precio,
            Double subtotal,
            Integer suborden,
            String nota
    ) {

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);

        // 🔥 nuevos campos correctos
        detalle.setPrecio(precio);
        detalle.setSubtotal(subtotal);
        detalle.setSuborden(suborden);

        detalle.setNota(nota);
        
        // 🔥 lógica de cocina
        detalle.setEnviadoCocina(true);
        detalle.setEstadoCocina(DetalleVenta.EstadoCocina.EN_ESPERA);

        return detalleVentaRepository.save(detalle);
    }
}
