package com.prueba.worker.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperUtils {

    private final static ObjectMapper _writterObjectMapper;
    private final static ObjectMapper _readerObjectMapper;

    static {
        _readerObjectMapper = new ObjectMapper();
        _writterObjectMapper = new ObjectMapper();
        _readerObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        _readerObjectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        //writter mapper in case of customization
    }

    public static ObjectMapper getReaderObjectMapper() {
        return _readerObjectMapper;
    }


    //any customization regarding dates or behavior when serializing objects to json

}
