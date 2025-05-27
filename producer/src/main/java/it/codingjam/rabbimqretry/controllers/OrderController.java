package it.codingjam.rabbimqretry.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.codingjam.rabbimqretry.controllers.dtos.OrderDto;
import it.codingjam.rabbimqretry.producers.RabbitMQProducer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderController.class);

    private final RabbitMQProducer rabbitMQProducer;

    public OrderController(RabbitMQProducer rabbitMQProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void createOrder(@RequestBody OrderDto order) throws JsonProcessingException {
        log.info("Creating order {}", order);
        rabbitMQProducer.sendMessage(order);
    }
}
