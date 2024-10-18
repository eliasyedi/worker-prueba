package handlers

import (
	"productos/internal/commons"
	"productos/internal/services"
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"github.com/gorilla/mux"
)




type handler struct {
	//for inyection of services, etc
   productosService  services.ProductosService
}

const (
	ProductosPrefix = "/productos"
)

// pass things for inyection
func NewProductosHandler(productosService services.ProductosService) *handler {
	return &handler{
        productosService: productosService,
    }
}

func (h *handler) RegisterHandlers(router *mux.Router) {
	subRouter := router.PathPrefix(ProductosPrefix).Subrouter()
	subRouter.HandleFunc("/producto/{id}", h.HandleGetProductById).Methods("GET")
}



func (h *handler) HandleGetProductById(w http.ResponseWriter, r *http.Request) {
    params := mux.Vars(r)
    id :=  params["id"]
	log.Printf("handle getproductosById with id %s \n", id)

    if id == ""{
        apiErr := commons.NewApiError(http.StatusBadRequest,errors.New(http.StatusText(http.StatusBadRequest)),false)
        writeJSON(w,http.StatusBadRequest,apiErr)
        return;
    }
    producto, err := h.productosService.GetProductById(id)
    if err != nil{
        if apiErr, ok := err.(commons.APIError); ok{
            writeJSON(w,apiErr.StatusCode,apiErr) 
            return
        }else {
            writeJSON(w,http.StatusBadRequest,commons.NewApiError(http.StatusBadRequest,err,false))
            return
        }
    }

    
    bytes, err := json.Marshal(producto)

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
