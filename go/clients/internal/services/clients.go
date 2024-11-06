package services

import (
	"clientes/internal/commons"
	"clientes/internal/db"
	"errors"
	"log"
	"net/http"
)

type ClientesService interface {
    GetClientById(clientId string) (db.Cliente,error);
    GetValidClientById(clientId string) (db.Cliente,error);
}

type ClientesMockService struct{
    storage  db.DbRepository
}


func NewClientesMockService(dataRepository db.DbRepository) *ClientesMockService{
   return &ClientesMockService{
        storage: dataRepository ,
    }
    
}



func (c *ClientesMockService)GetClientById(clientId string) (db.Cliente,error){
    cliente, err := c.storage.GetById(clientId)
    if err != nil{
        log.Println(err)
        return db.Cliente{}, commons.NewApiError(http.StatusBadRequest,errors.New("no se pudo obtener el cliente"),true) 
    }
    return cliente, nil
}

func (c * ClientesMockService)GetValidClientById(clientId string) (db.Cliente,error){
    cliente, err := c.storage.GetById(clientId)
    if err != nil{
        log.Println(err)
        return db.Cliente{}, commons.NewApiError(http.StatusBadRequest,errors.New("no se pudo obtener el cliente"),true) 
    }
    if cliente.Status != "active"{
        return db.Cliente{}, commons.NewApiError(http.StatusBadRequest,errors.New("cliente invalido"),true) 
    }
    return cliente, nil
}
