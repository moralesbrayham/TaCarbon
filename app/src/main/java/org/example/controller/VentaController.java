package org.example.controller;

import org.example.model.EstadoVenta;
import org.example.model.DetalleVenta;
import org.example.model.Venta;
import org.example.service.DetalleVentaService;
import org.example.service.VentaService;
import org.example.dto.VentaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    
    @Autowired
    private DetalleVentaService detalleVentaService;

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
    public ResponseEntity<?> actualizarEstado(
        @PathVariable Long id,
        @RequestParam String estado) {

        EstadoVenta nuevoEstado =
            EstadoVenta.valueOf(estado.toUpperCase());

        ventaService.actualizarEstado(id, nuevoEstado);

        return ResponseEntity.ok().build();
    }
    
    //Esto permite que nuestra app android agregue productos a cuentas abiertas usando ventaID
    @PostMapping("/{ventaId}/agregar-producto")
    public ResponseEntity<DetalleVenta> agregarProducto(
        @PathVariable Long ventaId,
        @RequestParam Long productoId,
        @RequestParam Integer cantidad) {

        DetalleVenta detalle = detalleVentaService.agregarProducto(ventaId, productoId, cantidad);
        return ResponseEntity.ok(detalle);
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
    
    //created today
    @GetMapping("/abiertas")
    public ResponseEntity<List<Venta>> obtenerVentasAbiertas(
        @RequestParam Long usuarioId,
        @RequestParam String rol) {

    return ResponseEntity.ok(
        ventaService.obtenerVentasAbiertas(usuarioId, rol)
    );
}
    
    @PostMapping("/crear-abierta")
    public ResponseEntity<Venta> crearVentaAbierta(
            @RequestParam Long usuarioId,
            @RequestParam Integer numeroMesa) {

        Venta venta = ventaService.crearVentaAbierta(usuarioId, numeroMesa);
        return ResponseEntity.ok(venta);
}
    
    @GetMapping("/abiertas/{usuarioId}")
    public ResponseEntity<List<Venta>> obtenerAbiertasPorUsuario(
            @PathVariable Long usuarioId) {

        List<Venta> ventas = ventaService.obtenerCuentasAbiertasPorUsuario(usuarioId);
        return ResponseEntity.ok(ventas);
    }
    
    
}