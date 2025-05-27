package it.codingjam.rabbimqretry.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
class JsonSerializer {

    private final ObjectMapper objectMapper;

    JsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> byte[] toByteArray(T data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
