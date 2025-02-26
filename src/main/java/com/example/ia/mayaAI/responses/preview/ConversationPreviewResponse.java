package com.example.ia.mayaAI.responses.preview;

import java.time.Instant;


public record ConversationPreviewResponse(
        String id,
        String title,
        String username,
        MessagePreviewResponse lastUserMessage,
        Instant createdAt,
        Instant updatedAt
) {
}
