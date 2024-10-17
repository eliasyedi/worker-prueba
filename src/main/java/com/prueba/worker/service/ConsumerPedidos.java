package com.prueba.worker.service;

import com.prueba.worker.pojo.PedidoMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ConsumerPedidos {
    private final static Sinks.Many<PedidoMessage> sink = Sinks.many().multicast().onBackpressureBuffer();


    //TODO ADD ERRORHANDLER for listener
    @KafkaListener(topics = "${custom.kafka.consumer.pedidos.topic.name}", groupId = "${custom.kafka.consumer.pedidos.topic.group-id")
    public void listen(ConsumerRecord<String,PedidoMessage> message
    ) {
        System.out.println(message);
        //TODO elias: logic for processing pedido should go here
//        Sinks.EmitResult result = sink.tryEmitNext(message.value());
//        if(result.isFailure()){
//           switch (result){
//               //TODO logging?
//               case FAIL_OVERFLOW -> System.out.println("buffer filled");
//               case FAIL_CANCELLED -> System.out.println("cancelled");
//               case FAIL_ZERO_SUBSCRIBER -> System.out.println("zero subscriber");
//               case FAIL_NON_SERIALIZED -> System.out.println("non serialized");
//               default -> System.out.println("unknown");
//           }
//        }

    }

    public Flux<PedidoMessage> getMessages() {
        return sink.asFlux();
    }
}
