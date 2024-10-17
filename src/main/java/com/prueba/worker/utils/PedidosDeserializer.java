package com.prueba.worker.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.worker.pojo.PedidoMessage;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class PedidosDeserializer implements Deserializer<PedidoMessage> {


    @Override
    public PedidoMessage deserialize(String s, byte[] bytes) {
        try{
            ObjectMapper om = ObjectMapperUtils.getReaderObjectMapper();
            return om.readValue(bytes, PedidoMessage.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
