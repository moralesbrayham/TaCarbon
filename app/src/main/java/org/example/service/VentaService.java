package org.example.service;

import org.example.model.EstadoVenta;
import org.example.model.DetalleVenta;
import org.example.model.Producto;
import org.example.model.Usuario;
import org.example.model.Venta;
import org.example.repository.ProductoRepository;
import org.example.repository.UsuarioRepository;
import org.example.repository.VentaRepository;
import org.example.repository.DetalleVentaRepository;
import org.example.dto.VentaDTO;
import org.example.dto.ItemDTO;
import org.example.dto.CocinaDTO;
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
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private TicketService ticketService; // ✅ Inyectado correctamente, sin "new"

    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public Venta crearVentaAbierta(Long usuarioId, Integer numeroMesa) {
        boolean existe = ventaRepository.existsByNumeroMesaAndEstado(
                numeroMesa, EstadoVenta.ABIERTA);
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

    // ✅ Un solo método para ventas abiertas — ADMIN ve todas, otros solo las suyas
    public List<Venta> obtenerVentasAbiertas(Long usuarioId, String rol) {
        if ("ADMIN".equals(rol)) {
            return ventaRepository.findByEstado(EstadoVenta.ABIERTA);
        }
        return ventaRepository.findByUsuarioIdAndEstado(usuarioId, EstadoVenta.ABIERTA);
    }

    @Transactional
    public Venta realizarVenta(Venta venta) {
        venta.setEstado(EstadoVenta.FINALIZADA);
        venta.setFecha(LocalDateTime.now());
        double totalVenta = 0;

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            detalle.setProducto(producto);
            detalle.setSubtotal(producto.getPrecio() * detalle.getCantidad());
            detalle.setVenta(venta);
            totalVenta += detalle.getSubtotal();
        }

        venta.setTotal(totalVenta);
        Venta nuevaVenta = ventaRepository.save(venta);

        String ticketPath = ticketService.generarTicketPDF(nuevaVenta); // ✅ Sin "new"
        System.out.println("Ticket generado en: " + ticketPath);

        return nuevaVenta;
    }

    @Transactional
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        venta.setEstado(EstadoVenta.ELIMINADA);
        ventaRepository.save(venta);

        String path = ticketService.generarTicketEliminadoPDF(venta); // ✅ Sin "new"
        System.out.println("Ticket eliminado generado en: " + path);
    }

    public List<VentaDTO> obtenerOrdenesPendientes() {
        List<Venta> ventas = ventaRepository.findByEstadoIn(
                List.of(EstadoVenta.ABIERTA, EstadoVenta.EN_PREPARACION));

        return ventas.stream().map(venta -> {
            VentaDTO dto = new VentaDTO();
            dto.setId(venta.getId());
            dto.setNumeroMesa(venta.getNumeroMesa());
            dto.setEstado(venta.getEstado().name());

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
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + idVenta));
        venta.setEstado(nuevoEstado);
        ventaRepository.save(venta);
    }

    public List<CocinaDTO> obtenerParaCocina() {
        return detalleVentaRepository.findByEstadoCocinaIn(
                List.of(DetalleVenta.EstadoCocina.EN_ESPERA,
                        DetalleVenta.EstadoCocina.EN_PREPARACION)
        ).stream().map(d -> {
            CocinaDTO dto = new CocinaDTO();
            dto.setId(d.getId());
            dto.setNombreProducto(d.getProducto().getNombre());
            dto.setCantidad(d.getCantidad());
            dto.setSuborden(d.getSuborden());
            dto.setEstadoCocina(d.getEstadoCocina().name());
            dto.setNota(d.getNota());
            dto.setNumeroMesa(d.getVenta().getNumeroMesa());
            dto.setVentaId(d.getVenta().getId());
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Transactional
    public Venta finalizarVentaAbierta(Long ventaId, Long usuarioId, Integer numeroMesa) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + ventaId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        venta.setUsuario(usuario);
        venta.setEstado(EstadoVenta.FINALIZADA);
        venta.setFecha(LocalDateTime.now());

        double total = venta.getDetalles().stream()
                .mapToDouble(d -> d.getPrecio() * d.getCantidad())
                .sum();
        venta.setTotal(total);

        Venta ventaFinalizada = ventaRepository.save(venta);

        String ticketPath = ticketService.generarTicketPDF(ventaFinalizada);
        System.out.println("Ticket generado en: " + ticketPath);

        return ventaFinalizada;
    }
}