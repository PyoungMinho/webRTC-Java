package com.example.webRTC;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoomController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}

