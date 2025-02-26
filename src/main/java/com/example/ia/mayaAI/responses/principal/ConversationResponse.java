package com.example.ia.mayaAI.responses.principal;

import com.example.ia.mayaAI.models.MessageModel;

import java.time.Instant;
import java.util.List;

public record ConversationResponse(
        String id, String username,
        String title,
        List<MessageModel> messages,
        Instant createdAt,
        Instant updatedAt
) {
}
