package com.prueba.worker.application;

import com.prueba.worker.service.ConsumerPedidos;
import com.prueba.worker.service.ProcesarPedidos;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup {

    private final ConsumerPedidos consumerPedidos;
    private final ProcesarPedidos procesarPedidos;

    public ApplicationStartup(ConsumerPedidos consumerPedidos, ProcesarPedidos procesarPedidos) {
        this.consumerPedidos = consumerPedidos;
        this.procesarPedidos = procesarPedidos;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startKafkaConsumer() {
        procesarPedidos.procesarPedidosKafka();
    }
}

