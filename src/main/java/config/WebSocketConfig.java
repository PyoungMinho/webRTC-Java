package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // WebSocket을 사용할것이다.
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); //topic으로 시작되는 요청을 구독한 모든 사용자들에게 메시지를 broadcast
        registry.setApplicationDestinationPrefixes("/app");      //app으로 시작되는 메시지는 라우팅
    }

    /*클라이언트가 웹 소켓 서버에 연결하는데 사용할 웹 소켓 엔드포인트 등록
	  withSockJS를 통해 웹 소켓을 지원하지 않는 브라우저에 대해 웹 소켓을 대체한다.
	  +)메소드명에 STOMP가 들어가는 경우 통신 프로토콜인 STOMP구현에서 작동된다. */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}
