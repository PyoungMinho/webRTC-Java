package com.example.webRTC.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class VideoCallController {

    @MessageMapping("/join")
    @SendTo("/topic/room")
    public String joinRoom(String roomName, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("roomName", roomName);
        return roomName + " joined!";
    }

    @MessageMapping("/offer")
    @SendTo("/topic/offer")
    public String handleOffer(String offer) {
        return offer;
    }

    @MessageMapping("/answer")
    @SendTo("/topic/answer")
    public String handleAnswer(String answer) {
        return answer;
    }

    @MessageMapping("/ice")
    @SendTo("/topic/ice")
    public String handleIceCandidate(String candidate) {
        return candidate;
    }
}