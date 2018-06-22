package com.zbest.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.*")
public class WebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketApplication.class, args);
	}
}
