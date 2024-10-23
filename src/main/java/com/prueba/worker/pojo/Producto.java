package com.prueba.worker.pojo;


public class Producto {

    private Long productoId;

    private String name;

    private Double price ;

    public Producto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
                "name='" + name + '\'' +
                ", productoId=" + productoId +
                ", price=" + price +
                '}';
    }
}

