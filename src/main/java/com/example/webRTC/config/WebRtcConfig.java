package com.example.webRTC.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

public class WebRtcConfig implements WebSocketConfigurer {



    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalingSocketHandler(), "/ws")   // 연결될 Endpoint
                .setAllowedOriginPatterns("*");                            // CORS 설정

    }

    @Bean
    public org.springframework.web.socket.WebSocketHandler signalingSocketHandler() {
        return new com.example.webRTC.handler.WebSocketHandler();
    }
}
