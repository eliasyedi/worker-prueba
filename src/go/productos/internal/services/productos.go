package services

import (
	"productos/internal/commons"
    "productos/internal/db"
	"errors"
	"log"
	"net/http"
)

type ProductosService interface {
    GetProductById(clientId string) (db.Producto,error);
}

type ProductosMockService struct{
    storage  db.DbRepository
}


func NewProductosMockService(dataRepository db.DbRepository) *ProductosMockService{
   return &ProductosMockService{
        storage: dataRepository ,
    }
    
}


func (c *ProductosMockService)GetProductById(clientId string) (db.Producto,error){
    producto, err := c.storage.GetById(clientId)
    if err != nil{
        log.Println(err)
        return db.Producto{}, commons.NewApiError(http.StatusBadRequest,errors.New("no se pudo obtener el producto"),true) 
    }
    return producto, nil
}

