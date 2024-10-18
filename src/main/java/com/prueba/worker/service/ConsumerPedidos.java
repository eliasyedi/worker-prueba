package com.prueba.worker.service;

import com.prueba.worker.pojo.PedidoMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class ConsumerPedidos {

    private final KafkaReceiver<String, PedidoMessage> kafkaReceiver;


    public ConsumerPedidos(KafkaReceiver<String, PedidoMessage> kafkaReceiver) {
        this.kafkaReceiver = kafkaReceiver;
    }




    public Flux<PedidoMessage> consumirPedidos() {
        return kafkaReceiver
                .receive()
                .map(ReceiverRecord::value);
    }
}
