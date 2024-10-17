package com.prueba.worker.service;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

public class ScalingKafkaConsumerService {


    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    public ScalingKafkaConsumerService(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }




}
