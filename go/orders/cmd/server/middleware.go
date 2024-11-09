package main

import (
	"log"
	"net/http"
)

//maybe its not required yet
func handlerNotFound(w http.ResponseWriter, r *http.Request){
    log.Println("discoveryHandler not found");
    /*
    w.WriteHeader(http.StatusMethodNotAllowed);
    */
    //prefer this over the top one
    http.Error(w,http.StatusText(http.StatusMethodNotAllowed),http.StatusMethodNotAllowed )
}


func contentTypeForResponse(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request){
        w.Header().Set("content-type", "application/json")
        next.ServeHTTP(w, r)
    })
}


