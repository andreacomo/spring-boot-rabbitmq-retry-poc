package it.codingjam.rabbimqretry.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class JsonDeserializer {

    private final ObjectMapper objectMapper;

    public JsonDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T fromByteArray(byte[] data, Class<T> clazz) {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
