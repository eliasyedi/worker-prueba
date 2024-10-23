package com.prueba.worker.dao;

import com.prueba.worker.pojo.PedidoMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PedidosRepository extends ReactiveMongoRepository<PedidoMessage,String> {
}
