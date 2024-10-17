package com.prueba.worker.controller;

import com.prueba.worker.pojo.PedidoMessage;
import com.prueba.worker.service.ConsumerPedidos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/pedidos")
public class PedidosController {

    ConsumerPedidos consumerPedidos;

    public PedidosController(ConsumerPedidos consumerPedidos) {
        this.consumerPedidos = consumerPedidos;
    }

    @GetMapping(value = "/mensajes")
    public Flux<PedidoMessage> streamMessages() {
        return consumerPedidos.getMessages()
                .doOnCancel(() -> System.out.println("Cancelled by client"))
                .doOnNext(message -> System.out.println("Flushing: " + message));  // Log the message
    }
}
