package com.prueba.worker.service;


import com.prueba.worker.dao.PedidosRepository;
import com.prueba.worker.dao.RedisDao;
import com.prueba.worker.pojo.Cliente;
import com.prueba.worker.pojo.PedidoMessage;
import com.prueba.worker.pojo.Producto;
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
    private final PedidosRepository pedidosRepository;

    public ProcesarPedidos(ConsumerPedidos consumerPedidos, WebClient.Builder webClientBuilder, RedisDao redisDao, PedidosRepository pedidosRepository) {
        this.consumerPedidos = consumerPedidos;
        this.webClientBuilder = webClientBuilder;
        this.redisDao = redisDao;
        this.pedidosRepository = pedidosRepository;
    }

    public void procesarPedidosKafka() {
        consumerPedidos.consumirPedidos()
                .flatMap(this::procesarPedido) // Process each message asynchronously
                .onErrorContinue((throwable, o) -> {
                    System.out.println("no se pudo procesar pedido: " + o);
                    System.out.println(throwable);
                })
                .subscribe();
    }


    //TODO change reference to the one it suits
    private Mono<Serializable> procesarPedido(PedidoMessage pedido) {
        System.out.println("Processing message: " + pedido);
        //if any pedido is not process correctly error should propagate
        Mono<Boolean> lockRedis = redisDao.lockPedido(pedido.getClientId(),pedido.getPedidoId());
        Mono<Boolean> unlockRedis = redisDao.unlockPedido(pedido.getClientId(),pedido.getPedidoId());
        Mono<PedidoMessage> insertDb = pedidosRepository.save(pedido);
        Mono<Cliente> clienteValido = validClient(pedido.getClientId());
        Mono<List<Producto>> productosValidados = Flux.fromIterable(pedido.getProductos())
                .flatMap(producto -> validProduct(producto.getProductoId()))
                .collectList();
//        cliente.then(productos);
        Mono<Boolean> checkClientProducts = Mono.zip(clienteValido,productosValidados)
                .flatMap(tuple -> {
                    //if theres any extra assertion it should be made here
                    //products and client are valid if they stream got this far
                    //this should be my client
                    System.out.println(tuple.getT1());
                    //this should be the product
                    System.out.println(tuple.getT2());
                    return Mono.just(Boolean.TRUE);
                });

        return lockRedis
                .flatMap(bool ->{
                    System.out.println("cliente procesando pedido" + pedido.getPedidoId());
                    //pedido is being process
                    if(bool){
                        return checkClientProducts;
                    }
                    //another procces has the lock
                    return Mono.error(new Throwable("pedido esta siendo procesado por otro worker"));
                })
                .flatMap(dd ->{
                    //TODO: database reflection
                    System.out.println("vamos a insertar pedido con id: " + pedido.getPedidoId());
                    return insertDb;
                })
                .flatMap(data ->{
                    //unlock in redis
                    System.out.println(data);
                    System.out.println("liberando lock a pedido con id: " + pedido.getPedidoId());
                    return unlockRedis;
                });

    }

    private Mono<Cliente> validClient(Long clientId) {
        System.out.println("realizando validacion de cliente");
        return webClientBuilder.baseUrl("http://localhost:3000/clientes").build()
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
                .bodyToMono(Cliente.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .filter(throwable -> throwable instanceof WebClientResponseException t && t.getStatusCode().is5xxServerError())
                        .jitter(0.5)  // Add jitter (50% of the backoff)
                        .maxBackoff(Duration.ofSeconds(5)));
    }

    private Mono<Producto> validProduct(Long productId){
        System.out.println("realizando validacion de producto");
        return webClientBuilder.baseUrl("http://localhost:3001/productos").build()
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
                .bodyToMono(Producto.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .filter(throwable -> throwable instanceof WebClientResponseException t && t.getStatusCode().is5xxServerError())
                        .jitter(0.5)  // Add jitter (50% of the backoff)
                        .maxBackoff(Duration.ofSeconds(5)));
    }


}
