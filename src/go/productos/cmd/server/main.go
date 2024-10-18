package main

import (
	"productos/config"
	"productos/internal/db"
	"productos/internal/handlers"
	"productos/internal/services"
	"context"
	"flag"
	"log"
	"net/http"
	"os"
	"os/signal"
	"time"

	"github.com/gorilla/mux"
	env "github.com/joho/godotenv"
)



func main(){
    var wait time.Duration
    flag.DurationVar(&wait, "graceful-timeout", time.Second * 15, "the duration for which the server gracefully wait for existing connections to finish - e.g. 15s or 1m")
    flag.Parse()

    
    mux:= mux.NewRouter()
    //middlewares are going to be execute in the order that they were set
   
    mux.Use(contentTypeForResponse)
    err := env.Load()
    if err!= nil{
        log.Println("could not load env file");
    }
    addrProducto:= config.EnvStringOrDef("ADDR_PRODUCTOS", "0.0.0.0:3000");

    //store
    store := db.NewInMemmoryStore(nil)

    //service
    productosService := services.NewProductosMockService(store)

    //discoveryHandler
    productosHandler:= handlers.NewProductosHandler(productosService);
    
    productosHandler.RegisterHandlers(mux);

    mux.HandleFunc("/", handlerNotFound)



    //server 
    srv := &http.Server{
        Addr:         addrProducto,
        // Good practice to set timeouts to avoid Slowloris attacks.
        WriteTimeout: time.Second * 15,
        ReadTimeout:  time.Second * 15,
        IdleTimeout:  time.Second * 60,
        Handler: mux, // Pass our instance of gorilla/mux in.
    }

    go func() {
        if err := srv.ListenAndServe(); err!=nil{
            log.Fatal("no pudo arrancar ", err); 
        }
    }()

    c := make(chan os.Signal, 1)
    // We'll accept graceful shutdowns when quit via SIGINT (Ctrl+C)
    // SIGKILL, SIGQUIT or SIGTERM (Ctrl+/) will not be caught.
    signal.Notify(c, os.Interrupt)

    // Block until we receive our signal.
    <-c

    // Create a deadline to wait for.
    ctx, cancel := context.WithTimeout(context.Background(), wait)
    defer cancel()
    // Doesn't block if no connections, but will otherwise wait
    // until the timeout deadline.
    srv.Shutdown(ctx)
    // Optionally, you could run srv.Shutdown in a goroutine and block on
    // <-ctx.Done() if your application should wait for other services
    // to finalize based on context cancellation.
    log.Println("shutting down")
    os.Exit(0)


}


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


