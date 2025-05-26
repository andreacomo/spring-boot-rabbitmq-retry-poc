package it.codingjam.rabbimqretry.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class JsonDeserializer {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public <T> T fromByteArray(byte[] data, Class<T> clazz) {
        return objectMapper.readValue(data, clazz);
    }
}
