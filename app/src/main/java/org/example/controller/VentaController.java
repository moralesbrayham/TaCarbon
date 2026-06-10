package org.example.controller;

import org.example.model.EstadoVenta;
import org.example.model.DetalleVenta;
import org.example.model.Venta;
import org.example.service.DetalleVentaService;
import org.example.service.VentaService;
import org.example.dto.VentaDTO;
import org.example.dto.CocinaDTO;
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
    private DetalleVentaService detalleVentaService;

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public List<Venta> obtenerTodasLasVentas() {
        return ventaService.obtenerTodasLasVentas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        return ventaService.obtenerVentaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pendientes")
    public List<VentaDTO> obtenerPendientes() {
        return ventaService.obtenerOrdenesPendientes();
    }

    @GetMapping("/cocina")
    public List<CocinaDTO> obtenerCocina() {
        return ventaService.obtenerParaCocina();
    }

    // ✅ Un solo endpoint para ventas abiertas — reemplaza los dos anteriores
    @GetMapping("/abiertas")
    public ResponseEntity<List<Venta>> obtenerVentasAbiertas(
            @RequestParam Long usuarioId,
            @RequestParam String rol) {
        return ResponseEntity.ok(ventaService.obtenerVentasAbiertas(usuarioId, rol));
    }

    @PostMapping("/crear-abierta")
    public ResponseEntity<Venta> crearVentaAbierta(
            @RequestParam Long usuarioId,
            @RequestParam Integer numeroMesa) {
        Venta venta = ventaService.crearVentaAbierta(usuarioId, numeroMesa);
        return ResponseEntity.ok(venta);
    }

    @PostMapping("/{ventaId}/agregar-producto")
    public ResponseEntity<DetalleVenta> agregarProducto(
            @PathVariable Long ventaId,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            @RequestParam Double precio,
            @RequestParam Double subtotal,
            @RequestParam Integer suborden,
            @RequestParam(required = false) String nota) {
        DetalleVenta detalle = detalleVentaService.agregarProducto(
                ventaId, productoId, cantidad, precio, subtotal, suborden, nota);
        return ResponseEntity.ok(detalle);
    }

    @PostMapping
    public ResponseEntity<?> realizarVenta(@RequestBody Venta venta) {
        try {
            Venta nuevaVenta = ventaService.realizarVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarVentaDesdeApp(@RequestBody Map<String, Object> body) {
        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            Integer numeroMesa = Integer.valueOf(body.get("numeroMesa").toString());
            Long ventaId = Long.valueOf(body.get("ventaId").toString());

            Venta venta = ventaService.finalizarVentaAbierta(ventaId, usuarioId, numeroMesa);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        // ✅ Se elimina la consulta redundante — el service ya lanza excepción si no existe
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        ventaService.actualizarEstado(id, EstadoVenta.valueOf(estado.toUpperCase()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/detalle/{id}/estado")
    public ResponseEntity<?> actualizarEstadoDetalle(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        detalleVentaService.actualizarEstadoDetalle(id, body.get("estadoCocina"));
        return ResponseEntity.ok().build();
    }
}