package com.example.webRTC.handler;

import org.json.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Map;

public class WebSocketHandler extends TextWebSocketHandler { // Java version signaling server

    // WebSocket 세션을 관리하는 Set (스레드 안전한 방식으로 관리)
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // 사용자 세션과 방 이름을 매핑하는 맵
    private final Map<String, String> userRooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 클라이언트가 연결될 때 세션을 추가
        sessions.add(session);
        System.out.println("새로운 세션 연결됨: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject jsonObject = new JSONObject(payload);
        String type = jsonObject.getString("type");

        switch (type) { // 타입으로 구분하여 알맞게 동작하도록 자바 코드로 signaling을 구현
            case "join_room":
                handleJoinRoom(session, jsonObject);
                break;
            case "offer":
                handleOffer(session, jsonObject);
                break;
            case "answer":
                handleAnswer(session, jsonObject);
                break;
            case "ice":
                handleIceCandidate(session, jsonObject);
                break;
            default:
                System.out.println("알 수 없는 메시지 타입: " + type);
        }
    }

    private void handleJoinRoom(WebSocketSession session, JSONObject jsonMessage) throws Exception {
        String roomName = jsonMessage.getString("roomName");
        userRooms.put(session.getId(), roomName);

        // "new_user" 메시지 브로드캐스트 (방에 새 사용자가 참가했다는 신호)
        broadcastMessage(roomName, session, new TextMessage("{\"type\": \"new_user\", \"data\": {\"user_id\": \"" + session.getId() + "\"}}"));
    }

    private void handleOffer(WebSocketSession session, JSONObject jsonMessage) throws Exception {
        String roomName = userRooms.get(session.getId());
        // Offer를 방의 다른 클라이언트에게 전달
        broadcastMessage(roomName, session, new TextMessage(jsonMessage.toString()));
    }

    private void handleAnswer(WebSocketSession session, JSONObject jsonMessage) throws Exception {
        String roomName = userRooms.get(session.getId());
        // Answer를 방의 다른 클라이언트에게 전달
        broadcastMessage(roomName, session, new TextMessage(jsonMessage.toString()));
    }

    private void handleIceCandidate(WebSocketSession session, JSONObject jsonMessage) throws Exception {
        String roomName = userRooms.get(session.getId());
        // ICE 후보를 방의 다른 클라이언트에게 전달
        broadcastMessage(roomName, session, new TextMessage(jsonMessage.toString()));
    }

    private void broadcastMessage(String roomName, WebSocketSession senderSession, TextMessage message) throws Exception {
        for (WebSocketSession session : sessions) {
            if (session.isOpen() && !session.getId().equals(senderSession.getId()) && roomName.equals(userRooms.get(session.getId()))) {
                session.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 클라이언트가 연결을 끊을 때 세션을 제거
        sessions.remove(session);
        userRooms.remove(session.getId());
        System.out.println("세션이 닫힘: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // WebSocket 연결 중 에러가 발생했을 때 호출
        System.err.println("전송 중 에러 발생: " + exception.getMessage());
        sessions.remove(session);
        userRooms.remove(session.getId());
        session.close(CloseStatus.SERVER_ERROR);
    }
}
