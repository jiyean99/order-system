package com.beyond.order_system.common.service;

import com.beyond.order_system.common.dto.SseMessageDto;
import com.beyond.order_system.common.repository.SseEmitterRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class SseAlarmService {
    /* *********************** DI 주입 *********************** */
    private final SseEmitterRegistry sseEmitterRegistry;
    private final ObjectMapper objectMapper;

    @Autowired
    public SseAlarmService(SseEmitterRegistry sseEmitterRegistry, ObjectMapper objectMapper) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(Long receiverId, Long senderId, String message) {
        SseEmitter sseEmitter = sseEmitterRegistry.getEmitter(receiverId);

        SseMessageDto dto = SseMessageDto.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .message(message)
                .build();

        String data = null;

        try {
            data = objectMapper.writeValueAsString(dto);
            sseEmitter.send(SseEmitter.event().name("ordered").data(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
