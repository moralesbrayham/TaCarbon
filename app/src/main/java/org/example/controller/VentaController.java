package org.example.controller;

import org.example.model.Venta;
import org.example.service.VentaService;
import org.example.dto.VentaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // Obtener todas las ventas
    @GetMapping
    public List<Venta> obtenerTodasLasVentas() {
        return ventaService.obtenerTodasLasVentas();
    }

    // Obtener una venta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        Optional<Venta> venta = ventaService.obtenerVentaPorId(id);
        return venta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/pendientes")
    public List<VentaDTO> obtenerPendientes() {
        return ventaService.obtenerOrdenesPendientes();
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String nuevoEstado = payload.get("estado");
        ventaService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok().build();
    }

    // Registrar una nueva venta
    @PostMapping
    public ResponseEntity<?> realizarVenta(@RequestBody Venta venta) {
        try {
            Venta nuevaVenta = ventaService.realizarVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Eliminar una venta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        if (!ventaService.obtenerVentaPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }
    
    
}