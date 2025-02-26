package com.example.ia.mayaAI.responses.preview;

import org.springframework.ai.chat.messages.MessageType;

import java.time.Instant;


public record MessagePreviewResponse(
        String id, MessageType type, String message, Instant createdAt
) {
}
