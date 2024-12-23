package com.prueba.worker.producer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@TestComponent
@EmbeddedKafka(partitions = 1, topics = {"${custom.kafka.consumer.pedidos.topic.name}"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class ProducerTest {

    private KafkaTemplate<String, String> kafkaTemplate;


}
