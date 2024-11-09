package main

import (
	"log"
	"net/http"
	"time"

	commons "github.com/eliasyedi/worker-prueba/go/commons/pkg"
	"github.com/eliasyedi/worker-prueba/go/orders/internal/handlers"
	"github.com/eliasyedi/worker-prueba/go/orders/internal/services"
	"github.com/gorilla/mux"
	env "github.com/joho/godotenv"
)


func NewHttpServer() *http.Server{

    mux:= mux.NewRouter()
    //middlewares are going to be execute in the order that they were set

    mux.Use(contentTypeForResponse)
    err := env.Load()
    if err!= nil{
        log.Println("could not load env file");
    }
    addrCliente:= commons.EnvStringOrDef("ADDR_ORDERS", "0.0.0.0:3000");

    //kafka
    producer := NewKafkaProducer()

    //service
    ordersService := services.NewOrdersService(producer)

    //discoveryHandler
    ordersHandler:= handlers.NewOrdersHandler(ordersService)

    ordersHandler.RegisterHandlers(mux);

    mux.HandleFunc("/", handlerNotFound)



    //server 
    return  &http.Server{
        Addr:         addrCliente,
        // Good practice to set timeouts to avoid Slowloris attacks.
        WriteTimeout: time.Second * 15,
        ReadTimeout:  time.Second * 15,
        IdleTimeout:  time.Second * 60,
        Handler: mux, // Pass our instance of gorilla/mux in.
    }
}
