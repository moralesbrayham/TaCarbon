package org.example.controller;

import org.example.service.ReporteService;
import org.example.model.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // Obtener ventas por fecha
    @GetMapping("/ventas/{fecha}") //fecha = año-mes-día
    public List<Venta> obtenerVentasPorFecha(@PathVariable String fecha) {
        return reporteService.obtenerVentasPorFecha(LocalDate.parse(fecha));
    }

    // Obtener productos más vendidos
    @GetMapping("/productos-mas-vendidos")
    public List<Object[]> obtenerProductosMasVendidos() {
        return reporteService.obtenerProductosMasVendidos();
    }

    // Obtener ingresos por periodo
    @GetMapping("/ingresos")
    public Double obtenerIngresos(@RequestParam String inicio, @RequestParam String fin) {
        return reporteService.obtenerIngresosPorPeriodo(LocalDate.parse(inicio), LocalDate.parse(fin));
    }
}

