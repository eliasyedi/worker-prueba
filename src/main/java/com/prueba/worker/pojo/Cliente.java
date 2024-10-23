package com.prueba.worker.pojo;


public class Cliente{

    private Long clientId;
    private String name;
    private String status;

    public Cliente() {
    }


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "clientId=" + clientId +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}