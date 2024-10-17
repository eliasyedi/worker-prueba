package com.prueba.worker.config;

import com.prueba.worker.utils.PedidosDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class AppConfig {


    @Value("${custom.kafka.consumer.quantity:3}")
    private Integer consumerQuantity;

    ConsumerFactory<String, String> consumerFactory;


    public AppConfig(ConsumerFactory<String, String> consumerFactory) {
        this.consumerFactory = consumerFactory;
    }

//    @Bean
//    public ConsumerFactory<String, String> consumerFactory(
//            @Value("${spring.kafka.bootstrap-servers") String bootstrapServers,
//            @Value("${spring.kafka.consumer.enable-auto-commit") Boolean enableAutoCommit
//    ) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PedidosDeserializer.class);
//        props.put(ErrorHandlingDeserializer.DESERIALIZER_CLASS_CONFIG, PedidosDeserializer.class);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
//
//        return new DefaultKafkaConsumerFactory<>(props);
//    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(consumerQuantity);
        return factory;
    }
}
