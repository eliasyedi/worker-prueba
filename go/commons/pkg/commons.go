package commons

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
)

type APIError struct{
    StatusCode int `json:"statusCode"`
    Msg string `json:"message"`
    UseApiMessage bool `json:"UseApiMessage"`
}

func (e APIError) Error() string{
    return fmt.Sprintf("api error, status:%d msg:%s",e.StatusCode,e.Msg)
}


func NewApiError(statusCode int, err error, useApiMessage bool) APIError{
    return APIError{
        StatusCode: statusCode,
        Msg: err.Error(),
        UseApiMessage: useApiMessage,
    }
}

func WriteJSON(w http.ResponseWriter, statusCode int, apiErr APIError){
    bytes, err := json.Marshal(apiErr)

    if err != nil{
        log.Printf(err.Error())
    }
    w.WriteHeader(statusCode)
    w.Write(bytes)
}
