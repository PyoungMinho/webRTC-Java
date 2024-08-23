//package config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration // 이 클래스가 Spring 설정 클래스임을 나타냄.
//@EnableWebSocketMessageBroker // WebSocket 메시지 브로커를 활성화하기 위한 어노테이션.
//@RequiredArgsConstructor // final 필드와 @NonNull 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션.
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // WebSocketMessageBrokerConfigurer를 구현하여 메시지 브로커를 설정하는 역할.
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/topic"); // "/topic"으로 시작하는 엔드포인트를 구독한 클라이언트에게 메시지를 전달하는 간단한 메시지 브로커를 활성화.
//        registry.setApplicationDestinationPrefixes("/app"); // "/app"으로 시작하는 메시지를 어플리케이션 내의 특정 핸들러로 라우팅.
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws") // 클라이언트가 연결할 WebSocket 엔드포인트를 "/ws"로 등록.
//                .setAllowedOrigins("*") // 모든 출처(도메인)에서의 WebSocket 요청을 허용.
//                .withSockJS(); // WebSocket을 지원하지 않는 브라우저를 위해 SockJS를 사용.
//    }
//}
//  스톰프를 사용하지 않아서 주석 처리
