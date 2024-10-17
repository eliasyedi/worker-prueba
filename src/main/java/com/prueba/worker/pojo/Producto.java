package com.prueba.worker.pojo;


public class Producto {

    private Long productoId;

    private String nombre;

    private Double precio ;

    public Producto() {
    }

    public Producto(String nombre, Double precio, Long productoId) {
        this.nombre = nombre;
        this.precio = precio;
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }


    @Override
    public String toString() {
        return "Producto{" +
                "nombre='" + nombre + '\'' +
                ", productoId=" + productoId +
                ", precio=" + precio +
                '}';
    }
}

