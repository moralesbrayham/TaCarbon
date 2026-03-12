package org.example.service;

import org.example.model.EstadoVenta;
import org.example.model.DetalleVenta;
import org.example.model.Producto;
import org.example.model.Usuario;
import org.example.model.Venta;

import org.example.repository.ProductoRepository;
import org.example.repository.UsuarioRepository;
import org.example.repository.VentaRepository;

import org.example.dto.VentaDTO;
import org.example.dto.ItemDTO;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Obtener todas las ventas
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    // Obtener una venta por ID
    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }
    
    //Crear venta que quedara en cuenta abierta
    public Venta crearVentaAbierta(Long usuarioId, Integer numeroMesa) {

        boolean existe = ventaRepository.existsByNumeroMesaAndEstado(
                numeroMesa,
                EstadoVenta.ABIERTA
        );

        if (existe) {
            throw new RuntimeException("Ya existe una cuenta abierta para esta mesa");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Venta venta = new Venta();
        venta.setNumeroMesa(numeroMesa);
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EstadoVenta.ABIERTA);
        venta.setUsuario(usuario);
        venta.setTotal(0.0);

        return ventaRepository.save(venta);
    }
    
    //Obtenemos las cuentas abiertas por usuario
    public List<Venta> obtenerCuentasAbiertasPorUsuario(Long usuarioId) {
        return ventaRepository.findByUsuarioIdAndEstado(
                usuarioId,
                EstadoVenta.ABIERTA
        );
    }

    // Registrar una nueva venta
    @Transactional
    public Venta realizarVenta(Venta venta) {
        // Set default status
        venta.setEstado(EstadoVenta.FINALIZADA);
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
        venta.setEstado(EstadoVenta.ELIMINADA);
        ventaRepository.save(venta);

        // Generar ticket de venta eliminada
     TicketService ticketService = new TicketService();
     String path = ticketService.generarTicketEliminadoPDF(venta);
        System.out.println("Ticket de venta eliminada generado en: " + path);
    }

    
    public List<VentaDTO> obtenerOrdenesPendientes() {

    List<EstadoVenta> estadosPermitidos = List.of(
        EstadoVenta.ABIERTA,
        EstadoVenta.EN_PREPARACION
    );

    List<Venta> ventas = ventaRepository.findByEstadoIn(estadosPermitidos);

    return ventas.stream().map(venta -> {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setNumeroMesa(venta.getNumeroMesa());
        dto.setEstado(venta.getEstado().name()); // 👈 convertir enum a String para el DTO

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

    
    public void actualizarEstado(Long idVenta, EstadoVenta nuevoEstado) {
    Optional<Venta> ventaOptional = ventaRepository.findById(idVenta);

    if (ventaOptional.isPresent()) {
        Venta venta = ventaOptional.get();
        venta.setEstado(nuevoEstado);
        ventaRepository.save(venta);
    } else {
        throw new RuntimeException("Venta no encontrada con ID: " + idVenta);
    }
}
    
    //created today
    public List<Venta> obtenerVentasAbiertas(Long usuarioId, String rol) {

    if (rol.equals("ADMIN")) {
        return ventaRepository.findByEstado(EstadoVenta.ABIERTA);
    }

    return ventaRepository.findByUsuarioIdAndEstado(usuarioId, EstadoVenta.ABIERTA);
}

    
}