package com.prueba.worker.service;

import com.prueba.worker.pojo.PedidoMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ConsumerPedidos {
    private final static Sinks.Many<PedidoMessage> sink = Sinks.many().multicast().onBackpressureBuffer();

    @KafkaListener(topics = "mensajes", groupId = "worker-group-1")
    public void listen(PedidoMessage message) {
        System.out.println(message);
        Sinks.EmitResult result = sink.tryEmitNext(message);
        if(result.isFailure()){
           switch (result){
               //TODO logging?
               case FAIL_OVERFLOW -> System.out.println("buffer filled");
               case FAIL_CANCELLED -> System.out.println("cancelled");
               case FAIL_ZERO_SUBSCRIBER -> System.out.println("zero subscriber");
               case FAIL_NON_SERIALIZED -> System.out.println("non serialized");
               default -> System.out.println("unknown");
           }
        }

    }

    public Flux<PedidoMessage> getMessages() {
        return sink.asFlux();
    }
}
