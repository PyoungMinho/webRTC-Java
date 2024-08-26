package com.example.webRTC.handler;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends TextWebSocketHandler {

    // 방(roomName) 관리 - 방 이름을 키로, 세션 맵을 값으로 가지는 구조
    private final Map<String, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // JSON 메시지 파싱
        String payload = message.getPayload();
        JSONObject jsonMessage = new JSONObject(payload);
        String type = jsonMessage.getString("type");

        if ("join_room".equals(type)) {
            String roomName = jsonMessage.getString("roomName");
            joinRoom(session, roomName);
        } else if ("offer".equals(type)) {
            String roomName = jsonMessage.getString("roomName");
            String offer = jsonMessage.getString("offer");
            sendMessageToRoom(roomName, session, "offer", offer);
        } else if ("answer".equals(type)) {
            String roomName = jsonMessage.getString("roomName");
            String answer = jsonMessage.getString("answer");
            sendMessageToRoom(roomName, session, "answer", answer);
        } else if ("ice".equals(type)) {
            String roomName = jsonMessage.getString("roomName");
            String candidate = jsonMessage.getString("candidate");
            sendMessageToRoom(roomName, session, "ice", candidate);
        }
    }

    private void joinRoom(WebSocketSession session, String roomName) throws Exception {
        rooms.computeIfAbsent(roomName, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
        JSONObject welcomeMessage = new JSONObject();
        welcomeMessage.put("type", "welcome");
        session.sendMessage(new TextMessage(welcomeMessage.toString()));
    }

    private void sendMessageToRoom(String roomName, WebSocketSession senderSession, String type, String data) throws Exception {
        Map<String, WebSocketSession> clients = rooms.get(roomName);
        if (clients != null) {
            JSONObject message = new JSONObject();
            message.put("type", type);
            message.put(type, data);

            for (WebSocketSession session : clients.values()) {
                if (!session.equals(senderSession)) {
                    session.sendMessage(new TextMessage(message.toString()));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // 연결이 닫히면 해당 세션을 제거
        rooms.values().forEach(sessions -> sessions.remove(session.getId()));
    }
}
//rooms 맵: 각 방에 포함된 클라이언트 세션을 관리하기 위한 맵입니다. roomName을 키로, 그 방에 속한 WebSocketSession을 값으로 가집니다.
//
//handleTextMessage 메서드: 클라이언트로부터 메시지를 수신했을 때, JSON을 파싱하여 type 필드를 확인한 후, 각 메시지 타입에 맞는 작업을 수행합니다.
//
//joinRoom 메서드: 클라이언트가 특정 방에 참가할 때, 방에 세션을 추가하고 "welcome" 메시지를 해당 클라이언트에게 전송합니다.
//
//sendMessageToRoom 메서드: 특정 방에 있는 다른 클라이언트들에게 메시지를 전송합니다. offer, answer, ice 메시지를 처리하는 로직이 여기에서 처리됩니다.