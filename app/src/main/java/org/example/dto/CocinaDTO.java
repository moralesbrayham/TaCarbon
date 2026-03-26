/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.dto;

public class CocinaDTO {

    private Long id;
    private String nombreProducto;
    private Integer cantidad;
    private Integer suborden;
    private String estadoCocina;

    private String nota;

    private Integer numeroMesa;
    private Long ventaId;

    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getSuborden() {
        return suborden;
    }

    public void setSuborden(Integer suborden) {
        this.suborden = suborden;
    }

    public String getEstadoCocina() {
        return estadoCocina;
    }

    public void setEstadoCocina(String estadoCocina) {
        this.estadoCocina = estadoCocina;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    
}