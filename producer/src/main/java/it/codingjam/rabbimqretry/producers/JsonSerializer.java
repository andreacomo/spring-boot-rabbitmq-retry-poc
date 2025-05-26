package it.codingjam.rabbimqretry.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class JsonSerializer {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public <T> byte[] toByteArray(T data) {
        return objectMapper.writeValueAsBytes(data);
    }
}
