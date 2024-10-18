package db

import (
	"errors"
	"fmt"
	"math/rand"
	"sync"
)


type StoreConfig struct{
    maxSize int
}

//some configuration can go here
func NewDefaultStoreConfig()*StoreConfig{
    return &StoreConfig{
        // -1 will not control size
        maxSize: -1,
    } 
}


func ConfigService(options ...func(*StoreConfig)) *StoreConfig{
    var config *StoreConfig = NewDefaultStoreConfig()
    //config := NewDefaultConfig()
    for _ , opt := range options {
        opt(config)
    }
    return config;
}



//type alias for client "db"
type ProductosDb map[string]Producto

//struct for storage synchronization
type Storage struct{
    options *StoreConfig
    lock sync.RWMutex
    store ProductosDb 
}


func populateDB(Productos ProductosDb){
    names := []string{"play 1","play 2","play 3","play 4","play 5","play 6"}
    indice:=rand.Int()%5
    indiceStatus:=rand.Int()%4
    prices := []float64{200.12,12222.33,13000.30,2000.11}
    for i := 1 ;i <10 ; i++{
        name:=names[indice] 
        price := prices[indiceStatus]
        //index of map is the Id
        Productos[fmt.Sprintf("%d",i)] = Producto{
            Name: name, 
            ProductoId: i,
            Price: price,
        }
        indice =rand.Int()%5
        indiceStatus =rand.Int()%2
    }
}


func NewInMemmoryStore(options *StoreConfig)*Storage{
    if options != nil{
        options = NewDefaultStoreConfig()
    }
    store := make(map[string]Producto)
    //dummy data
    populateDB(store)
    return &Storage{
        store: store, 
        options: options,
    }
}

    
func (s *Storage) GetById(id string) (Producto, error){
    //return error is redundant in this dummy case
    //lock to prevent dirty reads and writes for race condition
    s.lock.Lock()
    defer s.lock.Unlock()
    if value, has := s.store[id]; has{
        return value, nil
    }
    return Producto{},errors.New("producto no existe");
}

