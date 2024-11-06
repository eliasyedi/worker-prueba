package handlers

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"

	"github.com/eliasyedi/worker-prueba/go/commons"
	"github.com/gorilla/mux"
)




type handler struct {
	//for inyection of services, etc
   clientesService  services.ClientesService
}

const (
	ClientesPrefix = "/clientes"
)

// pass things for inyection
func NewClientesHandler(clientesService services.ClientesService) *handler {
	return &handler{
        clientesService: clientesService,
    }
}

func (h *handler) RegisterHandlers(router *mux.Router) {
	subRouter := router.PathPrefix(ClientesPrefix).Subrouter()
	subRouter.HandleFunc("/cliente/{id}", h.HandleGetClientById).Methods("GET")
	subRouter.HandleFunc("/cliente/valido/{id}", h.HandleGetValidClientById).Methods("GET")
}


func (h *handler) HandleGetValidClientById(w http.ResponseWriter, r *http.Request) {
    params := mux.Vars(r)
    id :=  params["id"]
	log.Printf("handle get with id %s \n", id)
    if id == ""{
        apiErr := commons.NewApiError(http.StatusBadRequest,errors.New(http.StatusText(http.StatusBadRequest)),false)
        writeJSON(w,http.StatusBadRequest,apiErr)
        return;
    }
    cliente, err := h.clientesService.GetValidClientById(id)
    if err != nil{
        if apiErr, ok := err.(commons.APIError); ok{
            writeJSON(w,apiErr.StatusCode,apiErr) 
            return
        }else {
            writeJSON(w,http.StatusBadRequest,commons.NewApiError(http.StatusBadRequest,err,false))
            return
        }
    }
    
    bytes, err := json.Marshal(cliente)

    if err != nil{
        log.Printf(err.Error())
        return
    }
    w.Write(bytes)


}

func (h *handler) HandleGetClientById(w http.ResponseWriter, r *http.Request) {
    params := mux.Vars(r)
    id :=  params["id"]
	log.Printf("handle getclientesById with id %s \n", id)

    if id == ""{
        apiErr := commons.NewApiError(http.StatusBadRequest,errors.New(http.StatusText(http.StatusBadRequest)),false)
        writeJSON(w,http.StatusBadRequest,apiErr)
        return;
    }
    cliente, err := h.clientesService.GetClientById(id)
    if err != nil{
        if apiErr, ok := err.(commons.APIError); ok{
            writeJSON(w,apiErr.StatusCode,apiErr) 
            return
        }else {
            writeJSON(w,http.StatusBadRequest,commons.NewApiError(http.StatusBadRequest,err,false))
            return
        }
    }

    
    bytes, err := json.Marshal(cliente)

    if err != nil{
        log.Printf(err.Error())
    }
    w.Write(bytes)
}


func writeJSON(w http.ResponseWriter, statusCode int, apiErr commons.APIError){
    bytes, err := json.Marshal(apiErr)

    if err != nil{
        log.Printf(err.Error())
    }
    w.WriteHeader(statusCode)
    w.Write(bytes)
}
