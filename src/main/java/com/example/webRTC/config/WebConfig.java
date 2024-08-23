package com.example.webRTC.config;

import com.example.webRTC.handler.WebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration // 이 클래스가 Spring 설정 클래스임을 나타냄.
@EnableWebSocket //webSocket을 사용하겠다.
//@RequiredArgsConstructor // final 필드와 @NonNull 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션.
public class WebConfig implements WebSocketConfigurer { // WebSocketConfigurer를 구현하여 WebSocket 핸들러를 등록하는 역할.

    // private final UserRepository userRepository; // 주석 처리된 코드: 이 프로젝트에서 사용되지 않지만 다른 프로젝트(Bm 프로젝트)를 위한 코드.

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/ws") // "/ws" 엔드포인트에 대해 WebSocketHandler를 등록.
                .setAllowedOrigins("*"); // 모든 출처(도메인)에서의 WebSocket 요청을 허용.
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler();
    }
    // @Bean
    // public WebSocketHandler webSocketHandler() {
    //     return new WebSocketHandler(userRepository);  // 주석 처리된 코드: 다른 프로젝트(Bm 프로젝트)에서 UserRepository를 사용하는 WebSocketHandler를 반환.
    // }
}

