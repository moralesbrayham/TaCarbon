/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.dto;

import java.util.List;
import org.example.dto.ItemDTO;

public class VentaDTO {
    private Long id;
    private int numeroMesa;
    private String estado;
    private List<ItemDTO> productos;

    // Constructor
    public VentaDTO(Long id, int numeroMesa, String estado, List<ItemDTO> productos) {
        this.id = id;
        this.numeroMesa = numeroMesa;
        this.estado = estado;
        this.productos = productos;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<ItemDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ItemDTO> productos) {
        this.productos = productos;
    }
}

