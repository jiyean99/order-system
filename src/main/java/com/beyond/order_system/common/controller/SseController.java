package com.beyond.order_system.common.controller;

import com.beyond.order_system.common.repository.SseEmitterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/sse")
public class SseController {
    /* *********************** DI 주입 *********************** */
    private final SseEmitterRegistry sseEmitterRegistry;

    @Autowired
    public SseController(SseEmitterRegistry sseEmitterRegistry) {
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @GetMapping("/connect")
    public SseEmitter connect(@AuthenticationPrincipal String principal) throws IOException {
        Long id = Long.parseLong(principal);
        SseEmitter sseEmitter = new SseEmitter(60 * 60 * 1000L); // 유효시간 : 1시간
        sseEmitterRegistry.addSseEmitter(id, sseEmitter);

        sseEmitter.send(SseEmitter.event().name("connect").data("연결완료"));
        return sseEmitter;
    }

}
