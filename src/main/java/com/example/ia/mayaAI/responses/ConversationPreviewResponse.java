package com.example.ia.mayaAI.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationPreviewResponse {

    private String id;
    private String title;
    private String username;
    private MessageResponse lastUserMessage;
    private Instant createdAt;
}
