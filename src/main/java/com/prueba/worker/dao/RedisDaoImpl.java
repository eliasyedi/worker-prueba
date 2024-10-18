package com.prueba.worker.dao;

import com.prueba.worker.pojo.PedidoMessage;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RedisDaoImpl implements RedisDao{

    ReactiveRedisTemplate<String, String> redisTemplate;
    ReactiveRedisTemplate<String, PedidoMessage> redisTemplatePedidos;

    public static final String CLIENTE_PPREFIX = "cliente";
    public static final String PEDIDO_PREFIX = "pedido";
    public static final String MENSAJE_PREFIX = PEDIDO_PREFIX + ".mensaje";
    public static final String CONTADOR_PREFIX = PEDIDO_PREFIX + ".mensaje.contador";


    public static String getPedidoKey(String pedidoId){
        return String.format("%s.%s", PEDIDO_PREFIX, pedidoId);
    }

    public static String getClienteKey(String clienteId){
        return String.format("%s.%s", CLIENTE_PPREFIX, clienteId );
    }
    public static String getPedidoMensajeKey(Long pedidoId){
        return String.format("%s.%s", MENSAJE_PREFIX, pedidoId);
    }
    public static String getPedidoMensajeKeyCounter(Long pedidoId){
        return String.format("%s.%s", CONTADOR_PREFIX, pedidoId);
    }

    public RedisDaoImpl(ReactiveRedisTemplate<String, PedidoMessage> reactiveRedisTemplatePedidos,ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.redisTemplate = reactiveRedisTemplate;
        this.redisTemplatePedidos = reactiveRedisTemplatePedidos;
    }

    public Mono<Boolean> lockPedido(Long clienteId, Long pedidoId){
        String clienteKey = getClienteKey(clienteId.toString());
        String pedidoKey = getClienteKey(pedidoId.toString());
        return redisTemplate.opsForValue().setIfAbsent(pedidoKey, clienteKey);
    }

    public Mono<Boolean> unlockPedido(Long clienteId, Long pedidoId){
        String clienteKey = getClienteKey(clienteId.toString());
        String pedidoKey = getClienteKey(pedidoId.toString());
        Mono<String> valor = redisTemplate.opsForValue().get(pedidoKey);
        Mono<Boolean> delete = redisTemplate.opsForValue().delete(pedidoKey);
        //checks if key belongs to caller
        return valor.flatMap(value -> {
            if (clienteKey.equals(value)) {
                return delete;
            }
            System.out.println("key no pertenece al llamador");
            return Mono.error(new RuntimeException("key no pertenece a llamador"));
        });
    }


    public Mono<Long> updatePedidosFallados(PedidoMessage pedidoMessage){
        String pedidoMensajeKey = getPedidoMensajeKey(pedidoMessage.getPedidoId());
        String pedidoMensajeKeyCounter = getPedidoMensajeKey(pedidoMessage.getPedidoId());
        return redisTemplate.hasKey(pedidoMensajeKey)
                .flatMap(value -> {
                    if(!value){
                        return redisTemplatePedidos.opsForValue().set(pedidoMensajeKey,pedidoMessage);
                    }
                    return Mono.just(false);
                })
                .then(redisTemplate.opsForValue().increment(pedidoMensajeKeyCounter));
    }
}
