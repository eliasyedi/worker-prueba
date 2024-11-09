package services

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
	commons "github.com/eliasyedi/worker-prueba/go/commons/pkg"
)

// holds information about the order
type Order struct{
    //generated orderId with snowflake
    OrderId int64  `json:"orderId"`
    //client if of client that made the order
    ClientId *int64 `json:"clientId"`
    //list of products 
    Products []Product  `json:"products"`
}


// holds information about the product
type Product struct{
    ProductId int64 `json:"productId"`
    Name string `json:"name"`
    Price float64 `json:"price"`
}

type OrdersService interface {
    PlaceOrder(order *Order) error;
}


type OrdersProducerService struct{
    ordersProducer *kafka.Producer
}

func NewOrdersService(producer *kafka.Producer) *OrdersProducerService{

   return &OrdersProducerService{
        ordersProducer: producer ,
    }
    
}


var topicOrders = "orders";

func (o *OrdersProducerService) PlaceOrder(order *Order) error{
    //logic for creating snowflakeId
    value, err :=  json.Marshal(order)
    if err != nil{
        return commons.NewApiError(http.StatusBadRequest,errors.New("could not parse order"),true) 
    }

    deliveryChannel := make(chan kafka.Event)
    message := &kafka.Message{
        TopicPartition: kafka.TopicPartition{ Topic:&topicOrders,Partition:kafka.PartitionAny},
        Value: value,
    }

    err = o.ordersProducer.Produce(message,deliveryChannel)
    if err != nil{
        return commons.NewApiError(http.StatusBadRequest,errors.New("could not produce message to broker"),true) 
    }
    
    event := <-deliveryChannel
    msg:=event.(*kafka.Message) 
    log.Printf("message produced: %s",msg)
    return nil;

}

