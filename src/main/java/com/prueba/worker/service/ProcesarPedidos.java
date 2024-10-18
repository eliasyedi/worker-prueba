package com.prueba.worker.service;


import com.prueba.worker.dao.RedisDao;
import com.prueba.worker.pojo.PedidoMessage;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

@Service
public class ProcesarPedidos {

    private final ConsumerPedidos consumerPedidos;
    private final WebClient.Builder webClientBuilder;
    private final RedisDao redisDao;

    public ProcesarPedidos(ConsumerPedidos consumerPedidos, WebClient.Builder webClientBuilder, RedisDao redisDao) {
        this.consumerPedidos = consumerPedidos;
        this.webClientBuilder = webClientBuilder;
        this.redisDao = redisDao;
    }


    public void procesarPedidosKafka() {
        consumerPedidos.consumirPedidos()
                .flatMap(this::procesarPedido) // Process each message asynchronously
                .subscribe();
    }


    private Mono<Serializable> procesarPedido(PedidoMessage pedido) {
        System.out.println("Processing message: " + pedido);
        Mono<Boolean> lockRedis = redisDao.lockPedido(pedido.getClientId(),pedido.getPedidoId());
        //TODO: string for now till i make a pojo to map
        Mono<String> cliente = validClient(pedido.getClientId());
        //TODO: string for now till i make a pojo to map
        Mono<List<String>> productos = Flux.fromIterable(pedido.getProductos())
                .flatMap(producto -> validProduct(producto.getProductoId()))
                .collectList();
        Mono<String> checkClientProducts = Mono.zip(cliente,productos)
                .flatMap(tuple -> {
                    //this should be my client
                    System.out.println(tuple.getT1());
                    //this should be the product
                    System.out.println(tuple.getT2());
                    return Mono.empty();
                });

       return lockRedis
                .flatMap(bool ->{
                    System.out.println("cliente procesando pedido" + pedido.getPedidoId());
                    if(bool){
                        return checkClientProducts;
                    }
                   //pedido is being process
                    return Mono.just(bool);
                });
    }

    private Mono<String> validClient(Long clientId) {
        System.out.println("realizando validacion de cliente");
        return webClientBuilder.baseUrl("localhost:3000/clientes").build()
                .get()
                .uri("/cliente/valido/{id}",clientId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    //TODO : mapper for status 400
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new WebClientResponseException("Client error: " + errorBody, response.statusCode().value(), "", null, null, null)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new WebClientResponseException("Server error: " + errorBody, response.statusCode().value(), "", null, null, null)));
                })
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .filter(throwable -> throwable instanceof WebClientResponseException t && t.getStatusCode().is5xxServerError())
                        .jitter(0.5)  // Add jitter (50% of the backoff)
                        .maxBackoff(Duration.ofSeconds(5)));
    }

    private Mono<String> validProduct(Long productId){
        System.out.println("realizando validacion de producto");
        return webClientBuilder.baseUrl("localhost:3001/productos").build()
                .get()
                .uri("/producto/{id}",productId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    //TODO : mapper for status 400
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new WebClientResponseException("Client error: " + errorBody, response.statusCode().value(), "", null, null, null)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new WebClientResponseException("Server error: " + errorBody, response.statusCode().value(), "", null, null, null)));
                })
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .filter(throwable -> throwable instanceof WebClientResponseException t && t.getStatusCode().is5xxServerError())
                        .jitter(0.5)  // Add jitter (50% of the backoff)
                        .maxBackoff(Duration.ofSeconds(5)));
    }


}
