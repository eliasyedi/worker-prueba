package commons;

import (
	"log"
	"os"
)

//return fallback if key its not found
func EnvStringOrDef(key, fallback string) string {
    value := os.Getenv(key)
    if value == ""{
        log.Printf("could not load enviroment variable, going with default [%s]",fallback) ;
        return fallback
    }
    log.Printf("got env variable [%s]", value);
    return value;
}


//exits if not found
func EnvString(key string) string {
    value := os.Getenv(key)

    if value == ""{
        log.Fatalf("could not load enviroment variable [%s]",key) ;
    }
    log.Printf("got env variable [%s]", value);
    return value;
    
}
