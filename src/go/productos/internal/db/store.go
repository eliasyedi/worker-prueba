package db


type Producto struct{
    ProductoId    int
    Name        string
    Price     float64 
}

type DbRepository interface{
    GetById(id string) (Producto, error);
}

