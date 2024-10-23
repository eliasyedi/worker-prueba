package db


type Producto struct{
    ProductoId    int `json:"productoId"`
    Name        string `json:"name"`
    Price     float64  `json:"price"`
}

type DbRepository interface{
    GetById(id string) (Producto, error);
}

