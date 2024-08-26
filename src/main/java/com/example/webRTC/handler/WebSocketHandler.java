package com.example.webRTC.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    // 세션 정보 저장  -> { 세션1 : 객체 , 세션2 : 객체 , ....}
    private final Map<String, WebSocketSession> sessions = new HashMap<>();






//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
//        String type = (String) payload.get("type");
//        String roomName = (String) payload.get("roomName");
//
//        switch (type) {
//            case "join_room":
//                handleJoinRoom(session,roomName);
//                break;
//            case "offer":
//            case "answer": // WebRTC answer 메시지일 때. B 클라이언트가 A 클라이언트의 offer에 대한 응답.
//            case "ice": // WebRTC ICE 후보 메시지일 때. 두 클라이언트가 서로 직접 연결될 수 있도록 네트워크 정보를 교환하는 데이터.
//                sendMessageToRoom(roomName, message); // 해당 방에 있는 모든 클라이언트에게 받은 메시지를 그대로 전송.
//                break;
//        }
//    }
//
//    private void handleJoinRoom(WebSocketSession session, String roomName) throws Exception {
//        rooms.computeIfAbsent(roomName, k -> new HashSet<>()).add(session);
//        session.getAttributes().put("roomName", roomName);
//
//        sendMessageToRoom(roomName, new TextMessage("{\"type\":\"welcome\"}"));
//    }
//    //
////    private void sendMessageToRoom(String roomName, TextMessage message) throws Exception {
////        Set<WebSocketSession> roomSessions = rooms.get(roomName);
////        if (roomSessions != null) {
////            for (WebSocketSession session : roomSessions) {
////                session.sendMessage(message);
////            }
////        }
////    }
//    private void sendMessageToRoom(String roomName, TextMessage message) {
//        Set<WebSocketSession> roomSessions = rooms.get(roomName);
//        if (roomSessions != null) {
//            for (WebSocketSession session : roomSessions) {
//                synchronized (session) {
//                    if (session.isOpen()) {
//                        try {
//                            session.sendMessage(message);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        // 연결된 세션에 대한 초기 작업은 handleTextMessage에서 처리됨
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
//        String roomName = (String) session.getAttributes().get("roomName");
//        if (roomName != null) {
//            Set<WebSocketSession> roomSessions = rooms.get(roomName);
//            if (roomSessions != null) {
//                roomSessions.remove(session);
//                if (roomSessions.isEmpty()) {
//                    rooms.remove(roomName);
//                }
//            }
//        }
//    }

}
