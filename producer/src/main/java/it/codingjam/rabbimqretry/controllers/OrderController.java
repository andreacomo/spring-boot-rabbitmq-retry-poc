package it.codingjam.rabbimqretry.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.codingjam.rabbimqretry.controllers.dtos.OrderDto;
import it.codingjam.rabbimqretry.producers.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final RabbitMQProducer rabbitMQProducer;

    @PostMapping
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void createOrder(@RequestBody OrderDto order) throws JsonProcessingException {
        log.info("Creating order {}", order);
        rabbitMQProducer.sendMessage(order);
    }
}
