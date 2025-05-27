package it.codingjam.rabbimqretry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class RabbitmqRetryConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqRetryConsumerApplication.class, args);
	}

}
