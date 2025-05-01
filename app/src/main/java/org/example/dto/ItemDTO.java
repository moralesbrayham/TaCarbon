/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.dto;

public class ItemDTO {

    private String nombreProducto;
    private int cantidad;
    private int suborden; 

    // Constructor
    public ItemDTO(String nombreProducto, int cantidad, int suborden) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.suborden = suborden;
    }

    public ItemDTO() {
    }

    // Getters y Setters
    
    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public int getSuborden() {
        return suborden;
    }

    public void setSuborden(int suborden) {
        this.suborden = suborden;
    }
}
