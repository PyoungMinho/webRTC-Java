package com.example.webRTC;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler { //핸들러 관련된 부분

    private Map<String, WebSocketSession> sessions = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");
        String roomName = (String) payload.get("roomName");

        switch (type) {
            case "join_room":
                session.getAttributes().put("roomName", roomName);
                sendMessageToRoom(roomName, new TextMessage("{\"type\":\"welcome\"}"));
                break;
            case "offer":
            case "answer":
            case "ice":
                sendMessageToRoom(roomName, message);
                break;
        }
    }

    private void sendMessageToRoom(String roomName, TextMessage message) throws Exception {
        for (WebSocketSession session : sessions.values()) {
            if (roomName.equals(session.getAttributes().get("roomName"))) {
                session.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }
}
