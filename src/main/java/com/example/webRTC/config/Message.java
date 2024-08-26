package com.example.webRTC.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    private String sender;    // 보내는 유저 UUID
    private String type;      // 메시지 타입
    private String receiver;  // 받는 사람
    private Long room;        // roomId
    private Object candidate; // 상태
    private Object sdp;       // sdp 정보

}
