package commons

import "fmt"

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
