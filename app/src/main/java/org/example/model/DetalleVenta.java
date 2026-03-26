package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonBackReference
    private Venta venta; //Reference to venta

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private Integer cantidad;
    
    private String nota;
    
    private Double precio;

    private Double subtotal;
    
    private Integer suborden;
    
    private Boolean enviadoCocina = false;

    @Enumerated(EnumType.STRING)
    private EstadoCocina estadoCocina;

    public enum EstadoCocina {
        EN_ESPERA,
        EN_PREPARACION,
        LISTO,
        ENTREGADO
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public Double getPrecio() { return precio;}
    public void setPrecio(Double precio) { this.precio = precio;}

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Integer getSuborden() { return suborden; }
    public void setSuborden(Integer Suborden) { this.suborden = Suborden; }
    
    public Boolean getEnviadoCocina() { return enviadoCocina; }
    public void setEnviadoCocina(Boolean enviadoCocina) { this.enviadoCocina = enviadoCocina; }

    public EstadoCocina getEstadoCocina() { return estadoCocina; }
    public void setEstadoCocina(EstadoCocina estadoCocina) { this.estadoCocina = estadoCocina; }
    
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    
}

