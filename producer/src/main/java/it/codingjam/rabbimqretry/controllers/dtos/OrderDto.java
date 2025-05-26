package it.codingjam.rabbimqretry.controllers.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderDto(
        OffsetDateTime orderDate,
        BigDecimal price
) {
}
