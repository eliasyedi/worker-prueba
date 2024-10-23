package com.prueba.worker.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prueba.worker.pojo.PedidoMessage;
import org.apache.kafka.common.serialization.Serializer;

public class PedidosSerializer implements Serializer<PedidoMessage> {

    @Override
    public byte[] serialize(String topic, PedidoMessage data) {

        try {
            return ObjectMapperUtils.getWritterObjectMapper().writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
