package com.prueba.worker.dao;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

@Repository
public class RedisDaoImpl implements RedisDao{

    ReactiveRedisTemplate<String, String> redisTemplate;

    public static final String CLIENTE_PPREFIX = "<##>";
    public static final String PEDIDO_PREFIX = "pedido.";


    public static String getProductoKey(String pedidoId){
        return String.format("%s.%s", PEDIDO_PREFIX, pedidoId);
    }

    public static String getClienteKey(String clienteId){
        return String.format("%s.%s", CLIENTE_PPREFIX, clienteId );
    }

    public RedisDaoImpl(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.redisTemplate = reactiveRedisTemplate;
    }

    public Mono<Boolean> lockPedido(Long clienteId, Long pedidoId){
        String clienteKey = getClienteKey(clienteId.toString());
        String pedidoKey = getClienteKey(pedidoId.toString());
        return redisTemplate.opsForValue().setIfAbsent(pedidoKey, clienteKey)
                .switchIfEmpty(Mono.error(new RuntimeException("Pedido locked")));
    }


}
