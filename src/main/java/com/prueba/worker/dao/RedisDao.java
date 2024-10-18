package com.prueba.worker.dao;

import com.prueba.worker.pojo.PedidoMessage;
import reactor.core.publisher.Mono;

public interface RedisDao {

     Mono<Boolean> lockPedido(Long clienteId, Long pedidoId);
     Mono<Boolean> unlockPedido(Long clienteId, Long pedidoId);
     Mono<Long> updatePedidosFallados(PedidoMessage pedidoMessage);
}
