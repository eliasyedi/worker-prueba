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
type ClientesDb map[string]Cliente

//struct for storage synchronization
type Storage struct{
    options *StoreConfig
    lock sync.RWMutex
    store ClientesDb 
}


func populateDB(clientesdB ClientesDb){
    names := []string{"elias","ruben","ignacio","artemicio","alfredo"}
    indice:=rand.Int()%5
    indiceStatus:=rand.Int()%2
    statuses := []string{"active","inactive"}
    for i := 1 ;i <10 ; i++{
        name:=names[indice] 
        status := statuses[indiceStatus]
        clientesdB[fmt.Sprintf("%d",i)] = Cliente{
            Name: name, 
            ClientId: i,
            Status: status,
        }
        indice =rand.Int()%5
        indiceStatus =rand.Int()%2
    }
}


func NewInMemmoryStore(options *StoreConfig)*Storage{
    if options != nil{
        options = NewDefaultStoreConfig()
    }
    store := make(map[string]Cliente)
    //dummy data
    populateDB(store)
    return &Storage{
        store: store, 
        options: options,
    }
}

    
func (s *Storage) GetById(id string) (Cliente, error){
    //return error is redundant in this dummy case
    //lock to prevent dirty reads and writes for race condition
    s.lock.Lock()
    defer s.lock.Unlock()
    if value, has := s.store[id]; has{
        return value, nil
    }
    return Cliente{},errors.New("cliente no existe");
}

