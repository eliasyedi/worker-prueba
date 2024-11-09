package main

import (
	"context"
	"flag"
	"log"
	"os"
	"os/signal"
	"time"

)



func main(){
    var wait time.Duration
    flag.DurationVar(&wait, "graceful-timeout", time.Minute * 5, "the duration for which the server gracefully wait for existing connections to finish - e.g. 15s or 1m")
    flag.Parse()

    
    srv := NewHttpServer()
    go func() {
        if err := srv.ListenAndServe(); err!=nil{
            log.Fatal("no pudo arrancar ", err); 
        }
    }()



    //here maybe can go a grpc listener


    

    //for shutting down
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


