package com.example.ia.mayaAI.models;

import lombok.*;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageModel{

    private UUID conversationId;

    private UUID id;

    private MessageType type;

    private String message;

    private LocalDateTime createdAt;
}
