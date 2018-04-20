package stu.lanyu.springdocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
@EnableFeignClients
@EnableCircuitBreaker
public class Runner {

	public static void main(String[] args) {
		SpringApplication.run(Runner.class, args);
	}
}
