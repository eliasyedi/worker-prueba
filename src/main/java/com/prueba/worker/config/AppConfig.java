package com.prueba.worker.config;

import com.prueba.worker.pojo.PedidoMessage;
import com.prueba.worker.utils.PedidosDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class AppConfig {


    @Value("${spring.kafka.consumer.group-id}")
    String groupId;
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    String bootstrapServers;
    @Value("${custom.kafka.consumer.pedidos.topic.name}")
    String topicName;



    @Bean
    public KafkaReceiver<String, PedidoMessage> kafkaReceiver(
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, PedidosDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        ReceiverOptions<String, PedidoMessage> receiverOptions = ReceiverOptions.<String, PedidoMessage>create(props)
                .subscription(Collections.singleton(topicName));
        return KafkaReceiver.create(receiverOptions);
    }




    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        RedisSerializationContext<String, String> context = RedisSerializationContext
                .<String, String>newSerializationContext(RedisSerializer.string())
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
    @Bean
    public ReactiveRedisTemplate<String, PedidoMessage> reactiveRedisTemplatePedidos(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<PedidoMessage> valueSerializer = new Jackson2JsonRedisSerializer<>(PedidoMessage.class);
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializationContext.RedisSerializationContextBuilder<String, PedidoMessage> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, PedidoMessage> context = builder
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}
