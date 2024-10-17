package com.prueba.worker.pojo;

import java.io.Serializable;
import java.util.List;


public class PedidoMessage implements Serializable {


    private Long pedidoId;
    private Long clientId;
    private List<Producto> productos;

    public PedidoMessage() {
    }

    public PedidoMessage(Long clientId, Long pedidoId, List<Producto> productos) {
        this.clientId = clientId;
        this.pedidoId = pedidoId;
        this.productos = productos;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "PedidoMessage{" +
                "clientId=" + clientId +
                ", pedidoId=" + pedidoId +
                ", productos=" + productos +
                '}';
    }
}
