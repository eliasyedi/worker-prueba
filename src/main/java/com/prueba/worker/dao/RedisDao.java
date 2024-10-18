package com.prueba.worker.dao;

import reactor.core.publisher.Mono;

public interface RedisDao {

     Mono<Boolean> lockPedido(Long clienteId, Long pedidoId);

}
