package org.example.service;

import org.example.model.Venta;
import org.example.model.DetalleVenta;
import org.example.repository.VentaRepository;
import org.example.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Obtener ventas diarias
    public List<Venta> obtenerVentasPorFecha(LocalDate fecha) {
        return ventaRepository.findByFechaBetween(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
    }

    // Obtener productos más vendidos
    public List<Object[]> obtenerProductosMasVendidos() {
        return ventaRepository.obtenerProductosMasVendidos();
    }

    // Obtener ingresos en un periodo
    public Double obtenerIngresosPorPeriodo(LocalDate inicio, LocalDate fin) {
        return ventaRepository.obtenerTotalIngresos(inicio.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }

}

