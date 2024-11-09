package handlers

import (
	"encoding/json"
	"io"
	"log"
	"net/http"

	commons "github.com/eliasyedi/worker-prueba/go/commons/pkg"
	"github.com/eliasyedi/worker-prueba/go/orders/internal/services"
	"github.com/gorilla/mux"
)




type handler struct {
	//for inyection of services, etc
   orderService  services.OrdersService
}

const (
	orderPrefix = "/order"
)

// pass things for inyection
func NewOrdersHandler(orderService services.OrdersService) *handler {
	return &handler{
        orderService: orderService,
    }
}

func (h *handler) RegisterHandlers(router *mux.Router) {
	subRouter := router.PathPrefix(orderPrefix).Subrouter()
	subRouter.HandleFunc("/order}", h.HandlePostPlaceOrder).Methods("POST")
}


func (h *handler) HandlePostPlaceOrder(w http.ResponseWriter, r *http.Request) {
    params := mux.Vars(r)
    id :=  params["id"]
	log.Printf("handle get with id %s \n", id)
    body, err := io.ReadAll(r.Body)
    if err != nil{
        apiErr := commons.NewApiError(http.StatusBadRequest,err,false)
        commons.WriteJSON(w,apiErr.StatusCode,apiErr)
        log.Println(apiErr) 
        return
    }
    /*if id == ""{
        apiErr := commons.NewApiError(http.StatusBadRequest,errors.New(http.StatusText(http.StatusBadRequest)),false)
        commons.WriteJSON(w,http.StatusBadRequest,apiErr)
        return;
    }*/
    order := &services.Order{}
    err = json.Unmarshal(body,order)
    if err != nil{
        log.Println(commons.NewApiError(http.StatusBadRequest,err,false)) 
        return
    }
    err = h.orderService.PlaceOrder(order)
    if err != nil{
        if apiErr, ok := err.(commons.APIError); ok{
            commons.WriteJSON(w,apiErr.StatusCode,apiErr) 
            return
        }else {
            commons.WriteJSON(w,http.StatusInternalServerError,commons.NewApiError(http.StatusInternalServerError,err,false))
            return
        }
    }
}

