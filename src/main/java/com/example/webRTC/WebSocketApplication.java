package com.example.webRTC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
public class WebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }

}
