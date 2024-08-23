package com.example.webRTC;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler { // WebSocket 메시지를 처리하는 핸들러 클래스 선언. TextWebSocketHandler를 상속받아 텍스트 메시지 처리 기능을 제공.

    private Map<String, WebSocketSession> sessions = new HashMap<>(); // 연결된 클라이언트들의 WebSocketSession을 저장하는 Map. 세션 ID를 키로 사용하여 각 클라이언트의 세션을 관리.
    private ObjectMapper objectMapper = new ObjectMapper(); // JSON 처리를 위한 Jackson 라이브러리의 ObjectMapper 객체 생성. 메시지를 JSON 형식으로 변환하거나, JSON 형식의 메시지를 Java 객체로 변환할 때 사용.

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception { // 클라이언트로부터 텍스트 메시지를 수신했을 때 호출되는 메서드.
        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class); // 수신한 메시지(payload)를 JSON에서 Java Map 객체로 변환.
        String type = (String) payload.get("type"); // 메시지 타입(type)을 추출. 예: "join_room", "offer", "answer", "ice".
        String roomName = (String) payload.get("roomName"); // 메시지에서 방 이름(roomName)을 추출.

        switch (type) { // 메시지 타입에 따라 다른 작업 수행.
            case "join_room": // 클라이언트가 방에 참여하려고 할 때.
                session.getAttributes().put("roomName", roomName); // 현재 세션에 방 이름을 저장.
                sendMessageToRoom(roomName, new TextMessage("{\"type\":\"welcome\"}")); // 해당 방에 있는 모든 클라이언트에게 환영 메시지를 전송.
                break;
            case "offer": // WebRTC offer 메시지일 때.
            case "answer": // WebRTC answer 메시지일 때.
            case "ice": // WebRTC ICE 후보 메시지일 때.
                sendMessageToRoom(roomName, message); // 해당 방에 있는 모든 클라이언트에게 받은 메시지를 그대로 전송.
                break;
        }
    }

    private void sendMessageToRoom(String roomName, TextMessage message) throws Exception { // 특정 방에 속한 모든 클라이언트에게 메시지를 전송하는 메서드.
        for (WebSocketSession session : sessions.values()) { // 모든 세션을 반복하여 검사.
            if (roomName.equals(session.getAttributes().get("roomName"))) { // 세션의 방 이름이 일치하는 경우.
                session.sendMessage(message); // 해당 클라이언트에게 메시지를 전송.
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception { // 클라이언트와의 WebSocket 연결이 성공적으로 수립되었을 때 호출되는 메서드.
        sessions.put(session.getId(), session); // 새로운 세션을 sessions Map에 추가하여 관리.
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception { // 클라이언트와의 WebSocket 연결이 종료되었을 때 호출되는 메서드.
        sessions.remove(session.getId()); // 종료된 세션을 sessions Map에서 제거.
    }
}
