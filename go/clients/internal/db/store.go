package db


type Cliente struct{
    ClientId    int `json:"clientId"`
    Name        string `json:"name"`
    Status      string `json:"status"`
}

type DbRepository interface{
    GetById(id string) (Cliente, error);
}

