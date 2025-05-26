# RabbitMQ retry consumer with Spring Boot

Two apps:
* `producer`: it exposes a REST API to enqueue a message to RabbitMQ (topic exchange `orders-exchange` with routing key `order.created`)
* `consumer`: RabbitMQ listener on incoming messages on `create-order-queue` (bound to `order.created` routing key)

Use cases:

* consumer success 
   ```
   curl 'http://localhost:8080/orders' \
   --header 'Content-Type: application/json' \
   --data '{
       "orderDate": "2025-05-26T15:00:00Z",
       "price": 15.90
   }'
   ```

* consumer retry, then DLQ 
   ```
   curl 'http://localhost:8080/orders' \
   --header 'Content-Type: application/json' \
   --data '{
       "orderDate": "2025-05-26T15:00:00Z",
       "price": 0
   }'
   ```
   just for testing: when price is 0, a `RuntimeException` is thrown, causing retry

* consumer direct to DLQ 
   ```
   curl 'http://localhost:8080/orders' \
   --header 'Content-Type: application/json' \
   --data '{
       "orderDate": "2025-05-26T15:00:00Z",
       "price": -1
   }'
   ```
   when the price is negative, `IllegalArgumentException` is thrown. This exception is registered as **non retryable**. See `RabbitMQRetryConfig` class for details

A unique DLQ has been configured (exchange `orders-exchange` -> queue `app-dead-letter-exchange` - no binding key). Every message will contain:
* Exception message
* Stacktrace
* Original consumer queue
* Original exchange
* Original binding key
* Original payload
