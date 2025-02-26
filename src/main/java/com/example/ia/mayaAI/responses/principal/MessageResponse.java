package com.example.ia.mayaAI.responses.principal;

import org.springframework.ai.chat.messages.MessageType;

import java.time.Instant;
import java.util.List;

public record MessageResponse(
        String id,
        String conversationId,
        MessageType type,
        String message,
        List<String> files,
        Instant createdAt
) {
}
