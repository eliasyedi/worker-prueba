package db


type Cliente struct{
    ClientId    int
    Name        string
    Status      string
}

type DbRepository interface{
    GetById(id string) (Cliente, error);
}

