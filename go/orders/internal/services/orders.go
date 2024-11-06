package services

import (
	"errors"
	"log"
	"net/http"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
	commons "github.com/eliasyedi/worker-prueba/go/commons/pkg"
)

// holds information about the order
type Order struct{
    //generated orderId with snowflake
    OrderId int64 
    //client if of client that made the order
    ClientId int64
    //list of products 
    Products []Product 
}


// holds information about the product
type Product struct{

    ProductId int64
    Name string
    Price float64
}

type OrdersService interface {
    CreateOrder(clientId string) (Order,error);
}


func NewClientesMockService(dataRepository db.DbRepository) *ClientesMockService{
   return &ClientesMockService{
        storage: dataRepository ,
    }
    
}



func (c *ClientesMockService)GetClientById(clientId string) (db.Cliente,error){
    cliente, err := c.storage.GetById(clientId)
    kafka.Producer
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
