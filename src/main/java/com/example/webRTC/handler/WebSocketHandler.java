package com.example.webRTC.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    // 세션 정보 저장  -> { 세션1 : 객체 , 세션2 : 객체 , ....}
    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    // 시그널링에 사용되는 메시지 타입 :
    private static final String MSG_TYPE_OFFER = "offer";
    private static final String MSG_TYPE_ANSWER = "answer";
    private static final String MSG_TYPE_CANDIDATE = "candidate";

    private static final String MSG_TYPE_JOIN = "join_room";




    @Override // WebSocket 연결 시
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("New connection established : 세션 {} ", session );

    }

    // 양방향 통신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");
        String roomName = (String) payload.get("roomName");


    }







    // 소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(">>> [ws] 클라이언트 접속 해제 : 세션 - {}, 상태 - {}", session, status);


    }

    // 소켓 통신 에러
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info(">>> 에러 발생 : 소켓 통신 에러 {}", exception.getMessage());


        String webSessionId = session.getId();

        // 연결이 종료되면 sessions 와 userInfo 에서 해당 유저 삭제
        sessions.get(webSessionId).close();
        sessions.remove(webSessionId);
    }


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
